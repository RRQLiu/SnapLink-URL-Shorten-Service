package com.snaplink.controller;

import org.example.BigtableConnector;
import org.example.IpPraser;
import org.example.UrlShortener;
import org.springframework.web.bind.annotation.*;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UrlController {
    private final UrlShortener urlShortener;
    private final IpPraser ipPraser;

    public UrlController() throws Exception {
        BigtableDataClient dataClient = BigtableConnector.connect();
        this.ipPraser = new IpPraser();
        this.urlShortener = new UrlShortener(dataClient);
    }

    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody UrlRequest request) throws Exception {
        return urlShortener.shortenUrl(
            request.getUserId(),
            request.getLongUrl(),
            java.time.LocalDate.now().toString()
        );
    }

    @GetMapping("/url/{shortUrl}")
    public String getOriginalUrl(
            @PathVariable String shortUrl,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress
    ) throws Exception {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);

        urlShortener.updateAnalytics(shortUrl, formattedDate, ipPraser.resolveGeolocation(ipAddress));
        return urlShortener.getOriginalUrl(shortUrl);
    }

    @GetMapping("/analysis")
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
}

// Request class
class UrlRequest {
    private String userId;
    private String longUrl;

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }
}

class LinkMetricsResponse {

    private String link;
    private ArrayList<Metric> metrics;

    // Getters and Setters
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ArrayList<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(ArrayList<Metric> metrics) {
        this.metrics = metrics;
    }

    public static class Metric {
        private String date;
        private int click;
        private ArrayList<Country> country;

        // Getters and Setters
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getClick() {
            return click;
        }

        public void setClick(int click) {
            this.click = click;
        }

        public ArrayList<Country> getCountry() {
            return country;
        }

        public void setCountry(ArrayList<Country> country) {
            this.country = country;
        }
    }

    public static class Country {
        private String name;
        private int click; // Fix the typo if needed to "click"

        public Country(String name, int click) {
            this.name = name;
            this.click = click;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getClick() { // Fix the typo if needed to "getClick"
            return click;
        }

        public void setClick(int click) { // Fix the typo if needed to "setClick"
            this.click = click;
        }
    }
}