package com.snaplink.dto;

public class ShortenUrlRequest {
    private String userId;
    private String longUrl;

    // Default constructor
    public ShortenUrlRequest() {}
    
    // All-args constructor
    public ShortenUrlRequest(String userId, String longUrl) {
        this.userId = userId;
        this.longUrl = longUrl;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}