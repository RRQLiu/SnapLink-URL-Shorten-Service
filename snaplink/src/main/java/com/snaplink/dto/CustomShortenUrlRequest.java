// CustomShortenUrlRequest.java
package com.snaplink.dto;

public class CustomShortenUrlRequest {
    private String userId;
    private String longUrl;
    private String customName;

    // Default constructor
    public CustomShortenUrlRequest() {}

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

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}