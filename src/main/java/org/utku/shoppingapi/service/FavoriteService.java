package org.utku.shoppingapi.service;

import org.utku.shoppingapi.dto.FavoriteDto;
import org.utku.shoppingapi.dto.response.ApiResponse;

import java.util.List;

/**
 * Service interface for favorite management operations.
 * Defines the contract for all favorite-related business logic and security.
 */
public interface FavoriteService {

    /**
     * [ADMIN / USER] Adds a product to a user's favorites list.
     * @param userId The ID of the user.
     * @param productId The ID of the product to add.
     * @return The newly created FavoriteDto.
     */
    FavoriteDto addFavorite(Long userId, Long productId);

    /**
     * [ADMIN / USER] Removes a product from a user's favorites list.
     * @param userId The ID of the user.
     * @param productId The ID of the product to remove.
     * @return A success response message.
     */
    ApiResponse<String> removeFavorite(Long userId, Long productId);

    /**
     * [ADMIN / USER] Retrieves all favorites for a specific user.
     * @param userId The ID of the user.
     * @return A list of the user's favorites as DTOs.
     */
    List<FavoriteDto> getFavoritesByUserId(Long userId);

    /**
     * [ADMIN / USER] Checks if a product is in a user's favorites.
     * @param userId The ID of the user.
     * @param productId The ID of the product to check.
     * @return A boolean indicating if the product is favorited.
     */
    boolean isFavorite(Long userId, Long productId);
}