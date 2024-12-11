// snaplink/src/test/java/com/snaplink/config/TestConfig.java
package com.snaplink.config;

import org.example.UrlShortener;
import org.example.BigtableConnector;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;

@TestConfiguration
public class TestConfig {
    @MockBean
    private BigtableDataClient bigtableDataClient;

    @Bean
    public UrlShortener urlShortener() {
        return new UrlShortener(bigtableDataClient);
    }
}