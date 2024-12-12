package com.snaplink.dto;

public class ShortenUrlResponse {
    private String shortUrl;

    // Default constructor
    public ShortenUrlResponse() {}

    // Constructor with shortUrl
    public ShortenUrlResponse(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    // Getter and setter
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}