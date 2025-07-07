package org.utku.shoppingapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Shopping API.
 * This class serves as the entry point for the Spring Boot application.
 * 
 * The Shopping API provides a comprehensive e-commerce backend with features including:
 * - User management and authentication
 * - Product catalog management
 * - Shopping cart functionality
 * - Order processing
 * - Favorite products management
 * 
 * The @SpringBootApplication annotation enables:
 * - @Configuration: Allows the class to define beans
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration mechanism
 * - @ComponentScan: Enables component scanning for the current package and sub-packages
 * 
 * @author Shopping API Team
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class ShoppingApiApplication {

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // .env dosyasını yükle
        Dotenv dotenv = Dotenv.load();

        // Ortam değişkenlerini sisteme ekle
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(ShoppingApiApplication.class, args);
    }

}
