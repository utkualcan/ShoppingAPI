package org.utku.shoppingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utku.shoppingapi.entity.Order;

import java.util.List;

/**
 * Repository interface for Order entity data access operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 * 
 * Provides methods for:
 * - Finding orders by user
 * - Managing order persistence and retrieval
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Finds all orders placed by a specific user.
     * Orders are typically returned in chronological order.
     * 
     * @param userId the ID of the user
     * @return List of orders placed by the user
     */
    List<Order> findByUserId(Long userId);
}