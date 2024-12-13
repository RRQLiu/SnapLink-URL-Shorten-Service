// BigtableUrlRepository.java
package com.snaplink.repository;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.snaplink.model.Url;
import org.example.UrlShortener;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class BigtableUrlRepository implements UrlRepository {
    private final UrlShortener urlShortener;
    private final BigtableDataClient dataClient;

    public BigtableUrlRepository(BigtableDataClient dataClient) {
        this.dataClient = dataClient;
        this.urlShortener = new UrlShortener(dataClient);
    }

    @Override
    public String saveUrl(Url url) {
        try {
            String shortUrl = urlShortener.shortenUrl(
                url.getUserId(),
                url.getLongUrl(),
                url.getCreatedAt().toString()
            );
            url.setShortUrl(shortUrl); // Update the url object with generated shortUrl
            return shortUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save URL", e);
        }
    }

    @Override
    public Optional<Url> findByShortUrl(String shortUrl) {
        try {
            String longUrl = urlShortener.getOriginalUrl(shortUrl);
            if (longUrl == null) {
                return Optional.empty();
            }
            
            String userId = "unknown"; 
            Url url = new Url(longUrl, userId);
            url.setShortUrl(shortUrl);
            
            return Optional.of(url);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch URL", e);
        }
    }

    @Override
    public void updateUrlStatus(String shortUrl, boolean isActive) {
        try {
            urlShortener.updateLinkStatus(shortUrl, isActive);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update URL status", e);
        }
    }

    @Override
    public void deleteUrl(String shortUrl) {
        try {
            urlShortener.deleteLink(shortUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete URL", e);
        }
    }

    @Override
    public void saveUser(String userId, String name, String email) {
        try {
            urlShortener.addUser(userId, name, email, LocalDate.now().toString(), "123", "123");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public void deleteUser(String userId) {
        try {
            urlShortener.deleteUser(userId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public void recordClick(String shortUrl, String date, String country) {
        try {
            urlShortener.updateAnalytics(shortUrl, date, country);
        } catch (IOException e) {
            throw new RuntimeException("Failed to record click", e);
        }
    }

    @Override
    public Map<String, Long> getClicksByDate(String shortUrl, LocalDate date) {
        try {
            Row analytics = urlShortener.getAnalytics(shortUrl, date.toString().replace("-", ""));
            return convertRowToClickMap(analytics);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get clicks by date", e);
        }
    }

    @Override
    public Map<String, Long> getClicksByDateRange(String shortUrl, LocalDate startDate, LocalDate endDate) {
        try {
            // Using the existing queryAnalytics method
            urlShortener.queryAnalytics(
                shortUrl,
                startDate.toString().replace("-", ""),
                endDate.toString().replace("-", "")
            );
            // Note: You might want to modify UrlShortener.queryAnalytics to return data
            // instead of printing it
            return new HashMap<>(); // Placeholder return
        } catch (IOException e) {
            throw new RuntimeException("Failed to get clicks by date range", e);
        }
    }

    @Override
    public Map<String, Long> getClicksByCountry(String shortUrl, LocalDate date) {
        try {
            Row analytics = urlShortener.getAnalytics(shortUrl, date.toString().replace("-", ""));
            return convertRowToCountryClickMap(analytics);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get clicks by country", e);
        }
    }

    // Helper method to convert Bigtable Row to click map
    private Map<String, Long> convertRowToClickMap(Row row) {
        Map<String, Long> clickMap = new HashMap<>();
        if (row != null) {
            row.getCells().forEach(cell -> {
                String qualifier = cell.getQualifier().toStringUtf8();
                if (qualifier.equals("total_clicks")) {
                    clickMap.put("total", Long.parseLong(cell.getValue().toStringUtf8()));
                }
            });
        }
        return clickMap;
    }

    // Helper method to convert Bigtable Row to country-specific click map
    private Map<String, Long> convertRowToCountryClickMap(Row row) {
        Map<String, Long> countryClickMap = new HashMap<>();
        if (row != null) {
            row.getCells().forEach(cell -> {
                String qualifier = cell.getQualifier().toStringUtf8();
                if (qualifier.startsWith("country:")) {
                    String country = qualifier.substring(8); // Remove "country:" prefix
                    countryClickMap.put(country, 
                        Long.parseLong(cell.getValue().toStringUtf8()));
                }
            });
        }
        return countryClickMap;
    }
}