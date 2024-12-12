// 5. Configuration for UrlShortener bean
package com.snaplink.config;

import org.example.UrlShortener;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlShortenerConfig {
    
    @Bean
    public UrlShortener urlShortener(BigtableDataClient bigtableDataClient) {
        return new UrlShortener(bigtableDataClient);
    }
}