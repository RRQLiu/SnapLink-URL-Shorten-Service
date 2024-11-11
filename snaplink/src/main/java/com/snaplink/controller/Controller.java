package com.snaplink.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class Controller {

    private final HashMap<String, String> tmpDataBase = new HashMap<>();
    
    @GetMapping("/")
    public String home() {
        return "Welcome to Snaplink Application!";
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Snaplink!";
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> createShortUrl(@RequestBody Map<String, String> requestBody) {
        String longUrl = requestBody.get("long_url");
        String customUrl = requestBody.get("custom_url");

        String shortUrl = this.shortenUrl(customUrl);
//        Link link = urlShortenerService.createShortUrl(longUrl, customUrl);

        Map<String, String> response = new HashMap<>();
        response.put("short_url", shortUrl);

        this.tmpDataBase.put(shortUrl, longUrl);
        System.out.print(longUrl + " " + this.tmpDataBase.get(shortUrl));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private String shortenUrl(String customUrl){
        String shortUrl;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        if(customUrl == null || customUrl.isEmpty()){
            StringBuilder shortUrlBuilder = new StringBuilder();
            Random random = new Random();
            for(int i = 0; i < 7; i++){
                shortUrlBuilder.append(chars.charAt(random.nextInt(chars.length())));
            }
            shortUrl = shortUrlBuilder.toString();
        }else{
            shortUrl = customUrl;
        }

        return shortUrl;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> resolveUrl(@PathVariable("shortUrl") String shortUrl) {
        String longUrl = this.tmpDataBase.get(shortUrl);
        HashMap<String, String> response = new HashMap<>();
        response.put("long_url", longUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}