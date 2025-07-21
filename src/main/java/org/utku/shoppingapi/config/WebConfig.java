package org.utku.shoppingapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.utku.shoppingapi.constants.AppConstants;

import java.util.List;

/**
 * Web configuration class for customizing Spring MVC behavior.
 * This configuration handles pagination settings for REST API endpoints.
 * 
 * @EnableSpringDataWebSupport enables Spring Data web support including:
 * - Automatic resolution of Pageable and Sort parameters from request parameters
 * - Support for PagedResourcesAssembler in controllers
 */
@Configuration
@EnableSpringDataWebSupport
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures pagination settings for the application.
     * This method sets up default pagination behavior for all pageable endpoints.
     * 
     * Default settings:
     * - Page size: 20 items per page
     * - Default sort: by 'id' field in ascending order
     * - Maximum page size: 100 items (prevents excessive data loading)
     * 
     * @param resolvers List of argument resolvers to which the pageable resolver is added
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver(); // Handles pagination arguments
        // Set default pagination using constants
        resolver.setFallbackPageable(PageRequest.of(0, AppConstants.DEFAULT_PAGE_SIZE, 
            Sort.by(AppConstants.DEFAULT_SORT_FIELD).ascending())); // Default: first page, default size, sort by id ascending
        // Prevent clients from requesting too many items at once
        resolver.setMaxPageSize(AppConstants.MAX_PAGE_SIZE); // Limit max page size for performance
        resolvers.add(resolver); // Add resolver to argument resolvers
    }
}