package org.utku.shoppingapi;

/**
 * Integration test for application context loading.
 * Verifies that the Spring Boot application context starts successfully.
 */

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ShoppingApiApplicationTests {

    /**
     * Tests that the Spring application context loads without errors.
     */
    @Test
    void contextLoads() {
    }

}
