package com.snaplink.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    // Click tracking
    void recordClick(String shortUrl, String ipAddress, String userAgent);
    
    // Basic analytics
    long getTotalClicks(String shortUrl);
    long getClicksForDate(String shortUrl, LocalDate date);
    
    // Time-based analytics
    Map<LocalDate, Long> getClicksByDateRange(String shortUrl, 
                                            LocalDate startDate, 
                                            LocalDate endDate);
    Map<String, Long> getHourlyClicksForDate(String shortUrl, LocalDate date);
    
    // Geographic analytics
    Map<String, Long> getClicksByCountry(String shortUrl);
    Map<String, Long> getClicksByCountryAndDate(String shortUrl, LocalDate date);
    
    // User analytics
    Map<String, Long> getUserUrlsPerformance(String userId);
    List<String> getTopPerformingUrls(String userId, int limit);
    
    // Performance metrics
    double getAverageClicksPerDay(String shortUrl, 
                                 LocalDate startDate, 
                                 LocalDate endDate);
    Map<String, Double> getUrlConversionRates(String userId);
}