package com.snaplink.dto;

import java.util.List;
import java.util.Map;

public class BulkShortenUrlResponse {
    private List<UrlMapping> urlMappings;
    private int successCount;
    private int failureCount;

    public static class UrlMapping {
        private String longUrl;
        private String shortUrl;
        private String status;
        private String error;

        public UrlMapping(String longUrl, String shortUrl, String status, String error) {
            this.longUrl = longUrl;
            this.shortUrl = shortUrl;
            this.status = status;
            this.error = error;
        }

        // Getters and setters
        public String getLongUrl() { return longUrl; }
        public void setLongUrl(String longUrl) { this.longUrl = longUrl; }
        public String getShortUrl() { return shortUrl; }
        public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    // Getters and setters
    public List<UrlMapping> getUrlMappings() { return urlMappings; }
    public void setUrlMappings(List<UrlMapping> urlMappings) { this.urlMappings = urlMappings; }
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
}
