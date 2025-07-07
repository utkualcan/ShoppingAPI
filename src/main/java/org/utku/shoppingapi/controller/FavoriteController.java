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

    @PostMapping
    @Operation(summary = "1. Add product to favorites")
    public ResponseEntity<FavoriteDto> addFavorite(@Valid @RequestBody FavoriteRequest request) {
        return ResponseEntity.ok(favoriteService.addFavorite(request.getUserId(), request.getProductId()));
    }

    @DeleteMapping
    @Operation(summary = "2. Remove product from favorites")
    public ResponseEntity<ApiResponse<String>> removeFavorite(@Valid @RequestBody FavoriteRequest request) {
        return ResponseEntity.ok(favoriteService.removeFavorite(request.getUserId(), request.getProductId()));
    }

    @PostMapping("/check")
    @Operation(summary = "3. Check if product is favorited")
    public ResponseEntity<Boolean> isFavorite(@Valid @RequestBody FavoriteRequest request) {
        return ResponseEntity.ok(favoriteService.isFavorite(request.getUserId(), request.getProductId()));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "4. Get user favorites")
    public ResponseEntity<List<FavoriteDto>> getFavoritesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUserId(userId));
    }
}