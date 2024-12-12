package com.snaplink.config;

import org.example.UrlShortener;
import org.example.BigtableConnector;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import org.mockito.Mockito;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public BigtableDataClient bigtableDataClient() {
        return Mockito.mock(BigtableDataClient.class);
    }
    
    @Bean
    public UrlShortener urlShortener(BigtableDataClient bigtableDataClient) {
        return new UrlShortener(bigtableDataClient);
    }
}