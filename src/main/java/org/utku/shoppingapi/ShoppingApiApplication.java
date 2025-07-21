package org.utku.shoppingapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the ShoppingAPI Spring Boot application.
 * <p>
 * Features:
 * - User authentication and registration
 * - Product management
 * - Shopping cart functionality
 * - Order processing
 * - Favorite products management
 * </p>
 * <p>
 * The @SpringBootApplication annotation enables:
 * - @Configuration: Allows the class to define beans
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration mechanism
 * - @ComponentScan: Enables component scanning for the current package and sub-packages
 * </p>
 *
 * @author Shopping API Team
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class ShoppingApiApplication {

    /**
     * Main method that starts the Spring Boot application.
     * Loads environment variables from .env file and sets them as system properties.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Load .env file
        Dotenv dotenv = Dotenv.load();

        // Add environment variables to system properties
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(ShoppingApiApplication.class, args);
    }

}
