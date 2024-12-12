package com.snaplink.dto;

import java.util.List;

public class BulkShortenUrlRequest {
    private String userId;
    private List<String> longUrls;

    // Constructor
    public BulkShortenUrlRequest() {}

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getLongUrls() {
        return longUrls;
    }

    public void setLongUrls(List<String> longUrls) {
        this.longUrls = longUrls;
    }
}