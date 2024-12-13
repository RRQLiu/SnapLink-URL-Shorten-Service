package com.snaplink.controller;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.gson.JsonObject;
import com.snaplink.service.UrlService;
import org.example.BigtableConnector;
import org.example.UrlShortener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UrlShortener urlShortener;

    public AuthController(UrlService urlService) throws Exception {
        BigtableDataClient dataClient = BigtableConnector.connect();
        this.urlShortener = new UrlShortener(dataClient);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody Map<String, String> user
    ) throws IOException {
        String email = user.get("email");
        String password = user.get("password");

        try {
            JsonObject userJson = urlShortener.getUser(email);
            if (!userJson.has("error")) {
                throw new IllegalArgumentException("Email already taken");
            }

            String salt = PasswordUtils.generateSalt();
            String hashedPassword = PasswordUtils.hashPassword(password, salt);

            // get current datetime
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);

            urlShortener.addUser(email, email, email, formattedDateTime, salt, hashedPassword);

            // Generate JWT
            String token = JwtTokenUtil.generateToken(email);

            return ResponseEntity.ok(Map.of("token", token, "userId", email));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> user
    ) throws IOException {
        String email = user.get("email");
        String password = user.get("password");
        try {
            JsonObject userJson = urlShortener.getUser(email);
            if (userJson.has("error")) {
                throw new IllegalArgumentException("Invalid email");
            }

            // Validate password
            String salt = userJson.get("salt").getAsString();
            String hashedPassword = PasswordUtils.hashPassword(password, salt);
            if (!hashedPassword.equals(userJson.get("hashedPassword").getAsString())){
                throw new IllegalArgumentException("Invalid email or password");
            }

            // Generate JWT
            String token = JwtTokenUtil.generateToken(email);
            return ResponseEntity.ok(Map.of("token", token, "userId", email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String token
    ) {
        // Implement token invalidation if needed
        return ResponseEntity.ok(Map.of("info", "Logout Successfully!"));
    }
}


class PasswordUtils {

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}


class JwtTokenUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}