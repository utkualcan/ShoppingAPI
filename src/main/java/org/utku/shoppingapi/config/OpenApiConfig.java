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
     * @return OpenAPI configuration object
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Name for JWT security scheme

        // Build and return OpenAPI configuration
        return new OpenAPI()
                .info(new Info()
                        .title("Shopping API") // API title
                        .version("1.0.0") // API version
                        .description("Comprehensive REST API for e-commerce shopping platform") // API description
                        .contact(new Contact()
                                .name("API Support") // Contact name
                                .email("support@shoppingapi.com"))) // Contact email
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // Add JWT security requirement
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName) // Security scheme name
                                                .type(SecurityScheme.Type.HTTP) // HTTP authentication type
                                                .scheme("bearer") // Bearer token scheme
                                                .bearerFormat("JWT") // JWT format
                                )
                );
    }
}