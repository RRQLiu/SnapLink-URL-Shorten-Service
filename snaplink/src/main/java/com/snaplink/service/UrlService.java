
package com.snaplink.service;

import com.snaplink.model.Url;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UrlService {
    // Core URL operations
    String createShortUrl(String userId, String longUrl);
    String getOriginalUrl(String shortUrl);
    void deactivateUrl(String shortUrl);
    void deleteUrl(String shortUrl);
    
    // Bulk operations
    Map<String, String> bulkCreateShortUrls(String userId, List<String> longUrls);
    Map<String, String> bulkGetOriginalUrls(List<String> shortUrls);
    
    // User operations
    void createUser(String userId, String name, String email);
    void deleteUser(String userId);
    List<Url> getUserUrls(String userId);
    
    // URL management
    void updateUrlStatus(String shortUrl, boolean isActive);
    Optional<Url> getUrlDetails(String shortUrl);
    boolean isUrlActive(String shortUrl);
}
