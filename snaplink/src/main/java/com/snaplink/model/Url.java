package com.snaplink.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Url {
    private String shortUrl;
    private String longUrl;
    private String userId;
    
    // Metadata 
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private String customAlias;  
    
    // Analytics fields // modify as you need 
    private long clickCount;
    private LocalDateTime lastAccessedAt;
    
    //some optional fields that might not be useful
    private String title;        
    private String description;  
    private String tags;        
    
    // Security/Privacy fields -> see if we need it or not
    private boolean isPrivate;   
    private String password;    
    private int maxClicks;      

    public Url(String longUrl, String userId) {
        this.longUrl = longUrl;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.clickCount = 0;
    }

    public Url(String longUrl, String userId, String customAlias) {
        this(longUrl, userId);
        this.customAlias = customAlias;
        this.shortUrl = customAlias; // Can be modified later by the service layer
    }

    // Getters and Setters
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxClicks() {
        return maxClicks;
    }

    public void setMaxClicks(int maxClicks) {
        this.maxClicks = maxClicks;
    }

    // Utility methods
    public void incrementClickCount() {
        this.clickCount++;
        this.lastAccessedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean hasReachedClickLimit() {
        return maxClicks > 0 && clickCount >= maxClicks;
    }

    public boolean isAccessible() {
        return isActive && !isExpired() && !hasReachedClickLimit();
    }

    // Object overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Url url = (Url) o;
        return shortUrl.equals(url.shortUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortUrl);
    }

    @Override
    public String toString() {
        return "Url{" +
                "shortUrl='" + shortUrl + '\'' +
                ", longUrl='" + longUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                ", clickCount=" + clickCount +
                '}';
    }

    // Builder pattern for flexible URL creation
    public static class Builder {
        private final Url url;

        public Builder(String longUrl, String userId) {
            url = new Url(longUrl, userId);
        }

        public Builder withCustomAlias(String customAlias) {
            url.setCustomAlias(customAlias);
            url.setShortUrl(customAlias);
            return this;
        }

        public Builder withExpiration(LocalDateTime expiresAt) {
            url.setExpiresAt(expiresAt);
            return this;
        }

        public Builder withPrivacy(boolean isPrivate) {
            url.setPrivate(isPrivate);
            return this;
        }

        public Builder withPassword(String password) {
            url.setPassword(password);
            return this;
        }

        public Builder withMaxClicks(int maxClicks) {
            url.setMaxClicks(maxClicks);
            return this;
        }

        public Builder withMetadata(String title, String description, String tags) {
            url.setTitle(title);
            url.setDescription(description);
            url.setTags(tags);
            return this;
        }

        public Url build() {
            return url;
        }
    }
}