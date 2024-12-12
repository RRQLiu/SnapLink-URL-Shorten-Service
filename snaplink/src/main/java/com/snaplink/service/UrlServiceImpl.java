// 3. Service Implementation - UrlServiceImpl.java
package com.snaplink.service;

import com.google.cloud.bigtable.data.v2.models.Row;
import com.snaplink.dto.UrlAnalyticsResponse;
import com.snaplink.exception.UrlNotFoundException;
import org.example.UrlShortener;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class UrlServiceImpl implements UrlService {
    private final UrlShortener urlShortener;
    
    public UrlServiceImpl(UrlShortener urlShortener) {
        this.urlShortener = urlShortener;
    }

    @Override
    public String createShortUrl(String userId, String longUrl, String ipAddress, String userAgent) {
        try {
            // Create short URL
            String shortUrl = urlShortener.shortenUrl(
                userId,
                longUrl,
                LocalDate.now().toString()
            );

            // Record initial analytics
            String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            String country = getCountryFromIP(ipAddress);
            urlShortener.updateAnalytics(shortUrl, date, country);

            return shortUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create short URL", e);
        }
    }

    @Override
    public String getAndRecordClick(String shortUrl, String ipAddress, String userAgent) {
        try {
            String originalUrl = urlShortener.getOriginalUrl(shortUrl);
            if (originalUrl == null) {
                throw new UrlNotFoundException("Short URL not found: " + shortUrl);
            }

            // Record analytics
            String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            String country = getCountryFromIP(ipAddress);
            urlShortener.updateAnalytics(shortUrl, date, country);

            return originalUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process URL redirection", e);
        }
    }

    private String getCountryFromIP(String ipAddress) {
        // TODO: Implement proper IP geolocation
        // For now, return a default value
        return "UNKNOWN";
    }

    @Override
     public UrlAnalyticsResponse getUrlAnalytics(String shortUrl, LocalDate startDate, LocalDate endDate) {
        try {
            Map<String, Long> dailyClicks = new TreeMap<>();
            Map<String, Long> clicksByCountry = new HashMap<>();
            long totalClicks = 0;

            String formattedStartDate = startDate.format(DateTimeFormatter.BASIC_ISO_DATE);
            String formattedEndDate = endDate.format(DateTimeFormatter.BASIC_ISO_DATE);

            // For each day in the range
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                String formattedDate = currentDate.format(DateTimeFormatter.BASIC_ISO_DATE);
                Row analyticsRow = urlShortener.getAnalytics(shortUrl, formattedDate);
                
                if (analyticsRow != null) {
                    // Process total clicks
                    List<String> cells = analyticsRow.getCells("metrics", "total_clicks")
                        .stream()
                        .map(cell -> cell.getValue().toStringUtf8())
                        .toList();

                    if (!cells.isEmpty()) {
                        long clicks = Long.parseLong(cells.get(0));
                        dailyClicks.put(currentDate.toString(), clicks);
                        totalClicks += clicks;
                    }

                    // Process country-specific clicks
                    analyticsRow.getCells()
                        .stream()
                        .filter(cell -> cell.getQualifier().toStringUtf8().startsWith("country:"))
                        .forEach(cell -> {
                            String country = cell.getQualifier().toStringUtf8().substring(8);
                            long clicks = Long.parseLong(cell.getValue().toStringUtf8());
                            clicksByCountry.merge(country, clicks, Long::sum);
                        });
                }

                currentDate = currentDate.plusDays(1);
            }

            return new UrlAnalyticsResponse(shortUrl, totalClicks, clicksByCountry, dailyClicks);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch analytics", e);
        }
    }
}