package com.snaplink.repository;

import com.snaplink.model.Url;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UrlRepository {
    // URL Operations
    String saveUrl(Url url);
    Optional<Url> findByShortUrl(String shortUrl);
    //List<Url> findByUserId(String userId);
    void updateUrlStatus(String shortUrl, boolean isActive);
    void deleteUrl(String shortUrl);
    
    // User Operations
    void saveUser(String userId, String name, String email);
    void deleteUser(String userId);
    
    // Analytics Operations
    void recordClick(String shortUrl, String date, String country);
    Map<String, Long> getClicksByDate(String shortUrl, LocalDate date);
    Map<String, Long> getClicksByDateRange(String shortUrl, LocalDate startDate, LocalDate endDate);
    Map<String, Long> getClicksByCountry(String shortUrl, LocalDate date);
}
