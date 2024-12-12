package com.snaplink.config;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import org.example.BigtableConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@Configuration
public class BigtableConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(BigtableConfig.class);
    
    @Bean(destroyMethod = "close")
    public BigtableDataClient bigtableDataClient() {
        try {
            BigtableDataClient client = BigtableConnector.connect();
            logger.info("Successfully connected to Bigtable");
            return client;
        } catch (IOException e) {
            logger.error("Failed to connect to Bigtable", e);
            throw new RuntimeException("Could not connect to Bigtable", e);
        }
    }
}