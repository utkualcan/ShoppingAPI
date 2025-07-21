package org.utku.shoppingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utku.shoppingapi.entity.Cart;

import java.util.List;

/**
 * Repository interface for Cart entity data access operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 *
 * Provides methods for:
 * - Finding carts by user ID
 * - Managing shopping cart persistence
 */
public interface CartRepository extends JpaRepository<Cart, Long> {
    /**
     * Finds all carts belonging to a specific user.
     * @param userId the ID of the user
     * @return List of carts owned by the user
     */
    List<Cart> findByUserId(Long userId);
    /**
     * Finds a cart by session ID for guest users.
     * @param sessionId the session ID
     * @return Optional containing the cart if found
     */
    java.util.Optional<Cart> findBySessionId(String sessionId);
}
