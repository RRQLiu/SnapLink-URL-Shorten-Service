package com.snaplink.dto;

import java.util.List;

public class UserLinksResponse {
    private String userId;
    private List<UserLinkInfo> links;
    private int totalLinks;

    public static class UserLinkInfo {
        private String shortUrl;
        private String longUrl;
        private String creationDate;
        private long totalClicks;
        private boolean active;

        // Constructor
        public UserLinkInfo(String shortUrl, String longUrl, String creationDate, long totalClicks, boolean active) {
            this.shortUrl = shortUrl;
            this.longUrl = longUrl;
            this.creationDate = creationDate;
            this.totalClicks = totalClicks;
            this.active = active;
        }

        // Getters and setters
        public String getShortUrl() { return shortUrl; }
        public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }
        public String getLongUrl() { return longUrl; }
        public void setLongUrl(String longUrl) { this.longUrl = longUrl; }
        public String getCreationDate() { return creationDate; }
        public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
        public long getTotalClicks() { return totalClicks; }
        public void setTotalClicks(long totalClicks) { this.totalClicks = totalClicks; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<UserLinkInfo> getLinks() { return links; }
    public void setLinks(List<UserLinkInfo> links) { this.links = links; }
    public int getTotalLinks() { return totalLinks; }
    public void setTotalLinks(int totalLinks) { this.totalLinks = totalLinks; }
}