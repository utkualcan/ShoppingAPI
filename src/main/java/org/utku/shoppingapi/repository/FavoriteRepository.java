package org.utku.shoppingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utku.shoppingapi.entity.Favorite;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Favorite entity data access operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 * 
 * Provides methods for:
 * - Finding favorites by user
 * - Checking if a product is favorited by a user
 * - Managing user-product favorite relationships
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * Finds all favorite products for a specific user.
     * 
     * @param userId the ID of the user
     * @return List of favorites belonging to the user
     */
    List<Favorite> findByUserId(Long userId);
    
    /**
     * Finds a specific favorite by user and product combination.
     * Used to check if a product is already favorited by a user.
     * 
     * @param userId the ID of the user
     * @param productId the ID of the product
     * @return Optional containing the favorite if found, empty otherwise
     */
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * Deletes a favorite by user and product combination.
     * Removes a product from user's favorites.
     * 
     * @param userId the ID of the user
     * @param productId the ID of the product to remove from favorites
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);
}