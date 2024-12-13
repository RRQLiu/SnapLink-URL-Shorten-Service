package com.snaplink.service;

import java.time.LocalDate;
import java.util.List;

import com.snaplink.dto.BulkShortenUrlResponse;
import com.snaplink.dto.UrlAnalyticsResponse;
import com.snaplink.dto.UserLinksResponse;

public interface UrlService {
    String createShortUrl(String userId, String longUrl, String ipAddress, String userAgent);
    String getAndRecordClick(String shortUrl, String ipAddress, String userAgent);
    UrlAnalyticsResponse getUrlAnalytics(String shortUrl, LocalDate startDate, LocalDate endDate);
    BulkShortenUrlResponse createBulkShortUrls(String userId, List<String> longUrls, 
                                                     String ipAddress, String userAgent);
    UserLinksResponse getUserLinks(String userId);
    String createCustomShortUrl(String userId, String longUrl, String customName, 
                              String ipAddress, String userAgent);

    String getLongUrl(String shortUrl);
    void deleteLink(String shortUrl, String userId);
}
