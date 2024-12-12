// snaplink/src/test/java/com/snaplink/SnapLinkApplicationTests.java
package com.snaplink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.snaplink.config.TestConfig;


@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test") 
class SnapLinkApplicationTests {

    @Test
    void contextLoads() {
        // Basic context load test
    }
}