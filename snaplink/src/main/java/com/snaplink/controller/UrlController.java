package com.snaplink.controller;

import org.example.BigtableConnector;
import org.example.UrlShortener;
import org.springframework.web.bind.annotation.*;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;

@RestController
@RequestMapping("/api")
public class UrlController {
    private final UrlShortener urlShortener;

    public UrlController() throws Exception {
        BigtableDataClient dataClient = BigtableConnector.connect();
        this.urlShortener = new UrlShortener(dataClient);
    }

    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody UrlRequest request) throws Exception {
        return urlShortener.shortenUrl(
            request.getUserId(),
            request.getLongUrl(),
            java.time.LocalDate.now().toString()
        );
    }

    @GetMapping("/url/{shortUrl}")
    public String getOriginalUrl(@PathVariable String shortUrl) throws Exception {
        return urlShortener.getOriginalUrl(shortUrl);
    }
}

// Request class
class UrlRequest {
    private String userId;
    private String longUrl;

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }
}