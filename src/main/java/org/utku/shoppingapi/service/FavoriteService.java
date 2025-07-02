package org.utku.shoppingapi.service;

import org.utku.shoppingapi.entity.Favorite;

import java.util.List;

/**
 * Service interface for favorite management operations.
 * Defines the contract for favorite-related business logic.
 */
public interface FavoriteService {
    
    /**
     * Adds a product to user's favorites.
     * 
     * @param userId The ID of the user
     * @param productId The ID of the product to add to favorites
     * @return The created favorite entity
     */
    Favorite addFavorite(Long userId, Long productId);
    
    /**
     * Removes a product from user's favorites.
     * 
     * @param userId The ID of the user
     * @param productId The ID of the product to remove from favorites
     */
    void removeFavorite(Long userId, Long productId);
    
    /**
     * Retrieves all favorite products for a specific user.
     * 
     * @param userId The ID of the user
     * @return List of favorite entities
     */
    List<Favorite> getFavoritesByUserId(Long userId);
    
    /**
     * Checks if a product exists in user's favorites.
     * 
     * @param userId The ID of the user
     * @param productId The ID of the product to check
     * @return true if product is favorited by user, false otherwise
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}