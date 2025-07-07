package org.utku.shoppingapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Swagger documentation.
 * This configuration customizes the API documentation appearance and ordering.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures OpenAPI documentation with custom info and server details.
     * * @return OpenAPI configuration object
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Güvenlik Şeması Tanımlaması (JWT Bearer Token)
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Shopping API")
                        .version("1.0.0")
                        .description("Comprehensive REST API for e-commerce shopping platform")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@shoppingapi.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server")))
                // Güvenlik Gereksinimini Ekle
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Güvenlik Komponentlerini Tanımla
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}