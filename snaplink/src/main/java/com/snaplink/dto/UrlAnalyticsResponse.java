package com.snaplink.dto;

import java.util.Map;

public class UrlAnalyticsResponse {
    private String shortUrl;
    private long totalClicks;
    private Map<String, Long> clicksByCountry;
    private Map<String, Long> dailyClicks;

    // Constructors
    public UrlAnalyticsResponse() {}

    public UrlAnalyticsResponse(String shortUrl, long totalClicks, 
                              Map<String, Long> clicksByCountry,
                              Map<String, Long> dailyClicks) {
        this.shortUrl = shortUrl;
        this.totalClicks = totalClicks;
        this.clicksByCountry = clicksByCountry;
        this.dailyClicks = dailyClicks;
    }

    // Getters and setters
    public String getShortUrl() { return shortUrl; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }
    public long getTotalClicks() { return totalClicks; }
    public void setTotalClicks(long totalClicks) { this.totalClicks = totalClicks; }
    public Map<String, Long> getClicksByCountry() { return clicksByCountry; }
    public void setClicksByCountry(Map<String, Long> clicksByCountry) { this.clicksByCountry = clicksByCountry; }
    public Map<String, Long> getDailyClicks() { return dailyClicks; }
    public void setDailyClicks(Map<String, Long> dailyClicks) { this.dailyClicks = dailyClicks; 
    }
}
