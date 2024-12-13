// 3. Service Implementation - UrlServiceImpl.java
package com.snaplink.service;

import com.google.bigtable.admin.v2.DropRowRangeRequest.TargetCase;
import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Value;
import com.google.protobuf.ByteString;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.TableId;
import com.snaplink.dto.BulkShortenUrlResponse;
import com.snaplink.dto.UrlAnalyticsResponse;
import com.snaplink.dto.UserLinksResponse;
import com.snaplink.exception.UrlNotFoundException;
import com.google.cloud.bigtable.data.v2.models.TableId;
import com.google.protobuf.ByteString;



import org.example.BigtableConnector;
import org.example.UrlShortener;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

    @Override
    public BulkShortenUrlResponse createBulkShortUrls(String userId, List<String> longUrls, 
                                                     String ipAddress, String userAgent) {
        if (longUrls.size() > 10) {
            throw new IllegalArgumentException("Cannot process more than 10 URLs at once");
        }

        List<BulkShortenUrlResponse.UrlMapping> mappings = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (String longUrl : longUrls) {
            try {
                String shortUrl = createShortUrl(userId, longUrl, ipAddress, userAgent);
                mappings.add(new BulkShortenUrlResponse.UrlMapping(
                    longUrl, shortUrl, "SUCCESS", null
                ));
                successCount++;
            } catch (Exception e) {
                mappings.add(new BulkShortenUrlResponse.UrlMapping(
                    longUrl, null, "FAILED", e.getMessage()
                ));
                failureCount++;
            }
        }

        BulkShortenUrlResponse response = new BulkShortenUrlResponse();
        response.setUrlMappings(mappings);
        response.setSuccessCount(successCount);
        response.setFailureCount(failureCount);
        return response;
    }

    @Override
    public UserLinksResponse getUserLinks(String userId) {
        try {
            List<Map<String, String>> linksData = urlShortener.getUserLinks(userId);
            
            List<UserLinksResponse.UserLinkInfo> linkInfos = linksData.stream()
                .map(data -> new UserLinksResponse.UserLinkInfo(
                    data.get("shortUrl"),
                    data.get(UrlShortener.getLongUrlKey()),
                    data.get(UrlShortener.getCreationDateKey()),
                    Long.parseLong(data.getOrDefault("totalClicks", "0")),
                    Boolean.parseBoolean(data.getOrDefault(UrlShortener.getStatusKey(), "true"))
                ))
                .collect(Collectors.toList());
    
            UserLinksResponse response = new UserLinksResponse();
            response.setUserId(userId);
            response.setLinks(linkInfos);
            response.setTotalLinks(linkInfos.size());
            
            return response;
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch user links", e);
        }
    }
    

@Override
public String createCustomShortUrl(String userId, String longUrl, String customName, 
                                 String ipAddress, String userAgent) {
    try {
        // Create custom short URL
        String shortUrl = urlShortener.createCustomUrl(
            userId,
            longUrl,
            customName,
            LocalDate.now().toString()
        );

        // Record initial analytics
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String country = getCountryFromIP(ipAddress);
        urlShortener.updateAnalytics(shortUrl, date, country);

        return shortUrl;
    } catch (IOException e) {
        throw new RuntimeException("Failed to create custom short URL", e);
    }
    }

    public String getLongUrl(String shortUrl)  {
        try {
            String longUrl = urlShortener.getOriginalUrl(shortUrl);
            if (longUrl == null) {
                throw new UrlNotFoundException("URL not found");
            }
            return longUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve long URL", e);
        }
    }

    public void deleteLink(String shortUrl, String userId) {

        try{
             // First, verify that the link belongs to the user
            List<Map<String, String>> userLinks = urlShortener.getUserLinks(userId);
        boolean isOwner = userLinks.stream()
            .anyMatch(link -> link.get("shortUrl").equals(shortUrl));
        
        if (!isOwner) {
            throw new IllegalArgumentException("You don't have permission to delete this link");
        }
        
        // Delete from link table
        urlShortener.deleteLink(shortUrl);
        
        // Delete all analytics data for this link
        urlShortener.deleteAnalytics(shortUrl);
    } catch (IOException e) {
        throw new RuntimeException("Failed to delete link", e);
        }
       
    }
}