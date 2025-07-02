package org.utku.shoppingapi.service;

import org.utku.shoppingapi.entity.Order;

import java.util.List;

/**
 * Service interface for order management operations.
 * Defines the contract for order-related business logic including:
 * - Order creation from shopping carts
 * - Order retrieval and history management
 * - Stock management during order processing
 */
public interface OrderService {
    
    /**
     * Creates a new order from the contents of a shopping cart.
     * Validates stock availability, creates order items, and clears the cart.
     * 
     * @param cartId The ID of the cart to convert to an order
     * @return The created order entity
     */
    Order createOrderFromCart(Long cartId);
    
    /**
     * Retrieves all orders placed by a specific user.
     * 
     * @param userId The ID of the user
     * @return List of orders placed by the user
     */
    List<Order> getOrdersByUserId(Long userId);
    
    /**
     * Finds a specific order by its ID.
     * 
     * @param orderId The ID of the order
     * @return The order entity
     */
    Order findOrderById(Long orderId);
}