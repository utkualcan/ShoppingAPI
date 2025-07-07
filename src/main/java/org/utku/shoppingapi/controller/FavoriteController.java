package org.utku.shoppingapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.utku.shoppingapi.dto.FavoriteDto;
import org.utku.shoppingapi.dto.request.FavoriteRequest;
import org.utku.shoppingapi.dto.response.ApiResponse;
import org.utku.shoppingapi.entity.Favorite;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing user favorite products.
 * This controller handles all HTTP requests related to favorite product management including:
 * - Adding products to favorites
 * - Removing products from favorites
 * - Retrieving user's favorite products
 * - Checking if a product is favorited
 * 
 * All endpoints are prefixed with '/api/favorites'.
 */
@RestController
@RequestMapping("/api/favorites")
@Tag(name = "5. Favorite Management", description = "API for managing user favorite products")
@PreAuthorize("hasRole('USER')")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final EntityMapper mapper;

    /**
     * Constructor for dependency injection.
     * 
     * @param favoriteService Service layer for favorite business logic
     * @param mapper Entity to DTO mapper for data transformation
     */
    public FavoriteController(FavoriteService favoriteService, EntityMapper mapper) {
        this.favoriteService = favoriteService;
        this.mapper = mapper;
    }

    /**
     * Adds a product to user's favorites.
     * 
     * @param request FavoriteRequest containing user ID and product ID
     * @return ResponseEntity containing the created FavoriteDto
     */
    @PostMapping
    @Operation(summary = "1. Add product to favorites", description = "Add a product to user's favorite list")
    public ResponseEntity<FavoriteDto> addFavorite(@Valid @RequestBody FavoriteRequest request) {
        return ResponseEntity.ok(mapper.toDto(favoriteService.addFavorite(request.getUserId(), request.getProductId())));
    }

    /**
     * Removes a product from user's favorites.
     * 
     * @param request FavoriteRequest containing user ID and product ID
     * @return ResponseEntity with success message
     */
    @DeleteMapping
    @Operation(summary = "2. Remove product from favorites", description = "Remove a product from user's favorite list")
    public ResponseEntity<ApiResponse<String>> removeFavorite(@Valid @RequestBody FavoriteRequest request) {
        favoriteService.removeFavorite(request.getUserId(), request.getProductId());
        return ResponseEntity.ok(ApiResponse.success("Product removed from favorites successfully", null));
    }

    /**
     * Checks if a product is in user's favorites.
     * 
     * @param request FavoriteRequest containing user ID and product ID
     * @return true if product is favorited by user, false otherwise
     */
    @PostMapping("/check")
    @Operation(summary = "3. Check if product is favorited", description = "Check if a specific product is in user's favorites")
    public boolean isFavorite(@Valid @RequestBody FavoriteRequest request) {
        return favoriteService.existsByUserIdAndProductId(request.getUserId(), request.getProductId());
    }

    /**
     * Retrieves all favorite products for a specific user.
     * 
     * @param userId The ID of the user
     * @return List of FavoriteDto objects representing user's favorites
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "4. Get user favorites", description = "Retrieve all favorite products for a specific user")
    public List<FavoriteDto> getFavoritesByUserId(@PathVariable Long userId) {
        return favoriteService.getFavoritesByUserId(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}