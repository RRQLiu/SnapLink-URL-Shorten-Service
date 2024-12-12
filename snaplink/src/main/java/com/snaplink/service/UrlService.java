// 2. Service Interface - UrlService.java
package com.snaplink.service;

import java.time.LocalDate;

import com.snaplink.dto.UrlAnalyticsResponse;

public interface UrlService {
    String createShortUrl(String userId, String longUrl, String ipAddress, String userAgent);
    String getAndRecordClick(String shortUrl, String ipAddress, String userAgent);
    UrlAnalyticsResponse getUrlAnalytics(String shortUrl, LocalDate startDate, LocalDate endDate);

}
