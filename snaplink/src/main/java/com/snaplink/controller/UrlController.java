package com.snaplink.controller;

import org.example.BigtableConnector;
import org.example.IpPraser;
import org.example.UrlShortener;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import org.springframework.http.HttpStatus;

import com.snaplink.dto.BulkShortenUrlRequest;
import com.snaplink.dto.BulkShortenUrlResponse;
import com.snaplink.dto.CustomShortenUrlRequest;
import com.snaplink.dto.ShortenUrlRequest;
import com.snaplink.dto.ShortenUrlResponse;
import com.snaplink.dto.UrlAnalyticsResponse;
import com.snaplink.dto.UserLinksResponse;
import com.snaplink.dto.LinkMetricsResponse;
import com.snaplink.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.time.LocalDate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:3000")  
public class UrlController {
    private final UrlShortener urlShortener;
    private final IpPraser ipPraser;
    private final UrlService urlService;


    public UrlController(UrlService urlService) throws Exception {
        BigtableDataClient dataClient = BigtableConnector.connect();
        this.ipPraser = new IpPraser();
        this.urlService = urlService;
        this.urlShortener = new UrlShortener(dataClient);
    }
   

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(
            @RequestBody ShortenUrlRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        
        String ipAddress = forwardedFor != null ? forwardedFor.split(",")[0] : "unknown";
        String shortUrl = urlService.createShortUrl(
            request.getUserId(), 
            request.getLongUrl(),
            ipAddress,
            userAgent
        );

        return ResponseEntity.ok(new ShortenUrlResponse(shortUrl));
    }

    @GetMapping("/url/{shortUrl}")
    public RedirectView getOriginalUrl(
            @PathVariable String shortUrl,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress
    ) throws Exception {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);

        urlShortener.updateAnalytics(shortUrl, formattedDate, ipPraser.resolveGeolocation(ipAddress));
        return new RedirectView(urlShortener.getOriginalUrl(shortUrl));
    }
    @GetMapping("/{shortUrl}")
    public RedirectView redirectToOriginalUrl(
            @PathVariable String shortUrl,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        
        String ipAddress = forwardedFor != null ? forwardedFor.split(",")[0] : "unknown";
        String longUrl = urlService.getAndRecordClick(shortUrl, ipAddress, userAgent);
        return new RedirectView(longUrl);
    }

    @GetMapping("/analytics/{shortUrl}")
    public ResponseEntity<UrlAnalyticsResponse> getUrlAnalytics(
            @PathVariable String shortUrl,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        UrlAnalyticsResponse analytics = urlService.getUrlAnalytics(shortUrl, start, end);
        return ResponseEntity.ok(analytics);
    }

    @PostMapping("/analysis")
    public LinkMetricsResponse getAnalysisMetrics(
            @RequestBody Map<String, String> requestBody
    ) throws IOException {
        String shortUrl = requestBody.get("shortUrl");
        String startDate = requestBody.getOrDefault("startDate", "19000101");
        String endDate = requestBody.getOrDefault("endDate", "20500101");

        ArrayList<HashMap<String, String>> analysisMetrics = urlShortener.queryAnalytics(
                shortUrl,
                startDate,
                endDate
        );

        LinkMetricsResponse response = new LinkMetricsResponse();
        response.setLink(shortUrl);

        ArrayList<LinkMetricsResponse.Metric> metrics = new ArrayList<>();
        analysisMetrics.forEach(day -> {
            LinkMetricsResponse.Metric metric = new LinkMetricsResponse.Metric();
            ArrayList<LinkMetricsResponse.Country> countries = new ArrayList<>();
            day.forEach((key, value) -> {
                if (key.startsWith("country")) {
                    String countryName = key.split(":")[1];
                    countries.add(new LinkMetricsResponse.Country(countryName, Integer.parseInt(value)));
                }
            });
            metric.setCountry(countries);
            metric.setDate(day.get("date"));
            metric.setClick(Integer.parseInt(day.get("total_clicks")));

            metrics.add(metric);
        });
        response.setMetrics(metrics);

        return response;
    }

    @PostMapping("/bulk-shorten")
    public ResponseEntity<BulkShortenUrlResponse> bulkShortenUrl(
            @RequestBody BulkShortenUrlRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        
        String ipAddress = forwardedFor != null ? forwardedFor.split(",")[0] : "unknown";
        BulkShortenUrlResponse response = urlService.createBulkShortUrls(
            request.getUserId(),
            request.getLongUrls(),
            ipAddress,
            userAgent
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/links")
    public ResponseEntity<UserLinksResponse> getUserLinks(
        @PathVariable String userId) {
    try {
        UserLinksResponse response = urlService.getUserLinks(userId);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        // You might want to add proper error handling here
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
@PostMapping("/custom-shorten")
public ResponseEntity<?> createCustomShortUrl(
        @RequestBody CustomShortenUrlRequest request,
        @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
        @RequestHeader(value = "User-Agent", required = false) String userAgent) {
    
    try {
        String ipAddress = forwardedFor != null ? forwardedFor.split(",")[0] : "unknown";
        
        // Additional validation if needed
        if (request.getCustomName() == null || request.getCustomName().trim().isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body("Custom name cannot be empty");
        }
        
        String shortUrl = urlService.createCustomShortUrl(
            request.getUserId(),
            request.getLongUrl(),
            request.getCustomName(),
            ipAddress,
            userAgent
        );

        return ResponseEntity.ok(new ShortenUrlResponse(shortUrl));
    } catch (IllegalArgumentException e) {
        return ResponseEntity
            .badRequest()
            .body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Failed to create custom short URL");
        }
    }
    @GetMapping("/retrieve/{shortUrl}")
    public ResponseEntity<Map<String, String>> retrieveOriginalUrl(@PathVariable String shortUrl) {
        try {
            String longUrl = urlService.getLongUrl(shortUrl);  // You'll need to add this method to UrlService
            Map<String, String> response = new HashMap<>();
            response.put("longUrl", longUrl);
        return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "Short URL not found"));
        }
}
}


