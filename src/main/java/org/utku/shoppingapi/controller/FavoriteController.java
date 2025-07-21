package org.utku.shoppingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.dto.FavoriteDto;
import org.utku.shoppingapi.dto.request.FavoriteRequest;
import org.utku.shoppingapi.dto.response.ApiResponse;
import org.utku.shoppingapi.service.FavoriteService;

import java.util.List;

/**
 * REST Controller for managing user favorite products.
 * Security is handled at the service layer.
 */
@RestController
@RequestMapping("/api/favorites")
@Tag(name = "5. Favorite Management", description = "API for managing user favorite products")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * Adds a product to the user's favorites list.
     *
     * @param request FavoriteRequest containing userId and productId
     * @return FavoriteDto representing the newly added favorite
     */
    @PostMapping
    @Operation(summary = "1. Add product to favorites", description = "Add a product to the user's favorites list")
    public ResponseEntity<FavoriteDto> addFavorite(@Valid @RequestBody FavoriteRequest request) {
        return ResponseEntity.ok(favoriteService.addFavorite(request.getUserId(), request.getProductId()));
    }

    /**
     * Removes a product from the user's favorites list.
     *
     * @param request FavoriteRequest containing userId and productId
     * @return ApiResponse with a success message
     */
    @DeleteMapping
    @Operation(summary = "2. Remove product from favorites", description = "Remove a product from the user's favorites list")
    public ResponseEntity<ApiResponse<String>> removeFavorite(@Valid @RequestBody FavoriteRequest request) {
        return ResponseEntity.ok(favoriteService.removeFavorite(request.getUserId(), request.getProductId()));
    }

    /**
     * Checks if a product is in the user's favorites list.
     *
     * @param request FavoriteRequest containing userId and productId
     * @return Boolean indicating if the product is favorited
     */
    @PostMapping("/check")
    @Operation(summary = "3. Check if product is favorited", description = "Check if a product is in the user's favorites list")
    public ResponseEntity<Boolean> isFavorite(@Valid @RequestBody FavoriteRequest request) {
        return ResponseEntity.ok(favoriteService.isFavorite(request.getUserId(), request.getProductId()));
    }

    /**
     * Retrieves all favorite products for a specific user.
     *
     * @param userId ID of the user
     * @return List of FavoriteDto objects
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "4. Get user favorites", description = "Retrieve all favorite products for a specific user")
    public ResponseEntity<List<FavoriteDto>> getFavoritesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUserId(userId));
    }
}