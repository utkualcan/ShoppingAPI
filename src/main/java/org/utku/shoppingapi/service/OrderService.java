package org.utku.shoppingapi.service;

import org.utku.shoppingapi.dto.OrderDto;
import org.utku.shoppingapi.dto.SimpleOrderDto;
import java.util.List;

/**
 * Service interface for order management operations.
 * Defines the contract for order-related business logic.
 */
public interface OrderService {

    /**
     * Creates a new order from the contents of a shopping cart.
     * @param cartId The ID of the cart to convert into an order.
     * @return The created order as a DTO.
     */
    OrderDto createOrderFromCart(Long cartId);

    /**
     * Retrieves all orders in the system. Intended for ADMIN use only.
     * @return A list of all orders as DTOs.
     */
    List<OrderDto> findAllOrders();

    /**
     * Retrieves all orders for a specific user.
     * @param userId The ID of the user whose orders to retrieve.
     * @return A list of the user's orders as DTOs.
     */
    List<OrderDto> findOrdersByUserId(Long userId);

    /**
     * Finds a specific order by its ID.
     * @param orderId The ID of the order.
     * @return The order as a DTO.
     */
    OrderDto findOrderById(Long orderId);

    /**
     * Finds a specific order by its ID and returns it in a simplified format.
     * @param orderId The ID of the order.
     * @return The order as a SimpleOrderDto.
     */
    SimpleOrderDto findSimpleOrderById(Long orderId);
}