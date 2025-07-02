package org.utku.shoppingapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.dto.OrderDto;
import org.utku.shoppingapi.dto.SimpleOrderDto;
import org.utku.shoppingapi.entity.Order;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing order operations.
 * This controller handles all HTTP requests related to order management including:
 * - Creating orders from shopping carts
 * - Retrieving order information
 * - Managing order history for users
 * 
 * All endpoints are prefixed with '/api/orders'.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "4. Order Management", description = "API for managing orders and order history")
public class OrderController {

    private final OrderService orderService;
    private final EntityMapper mapper;

    /**
     * Constructor for dependency injection.
     * 
     * @param orderService Service layer for order business logic
     * @param mapper Entity to DTO mapper for data transformation
     */
    public OrderController(OrderService orderService, EntityMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    /**
     * Retrieves a specific order by its ID.
     * 
     * @param orderId The ID of the order
     * @return ResponseEntity containing the OrderDto if found
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "1. Get order by ID", description = "Retrieve a specific order by its unique identifier")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(mapper.toDto(orderService.findOrderById(orderId)));
    }
    
    /**
     * Retrieves a specific order in simplified format (less IDs, more readable).
     * 
     * @param orderId The ID of the order to retrieve
     * @return ResponseEntity containing the SimpleOrderDto
     */
    @GetMapping("/{orderId}/simple")
    @Operation(summary = "2. Get order (simple format)", description = "Retrieve order with simplified structure and fewer ID references")
    public ResponseEntity<SimpleOrderDto> getSimpleOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(mapper.toSimpleDto(orderService.findOrderById(orderId)));
    }

    /**
     * Creates a new order from the contents of a shopping cart.
     * Converts all cart items into order items and calculates the total amount.
     * 
     * @param cartId The ID of the cart to convert to an order
     * @return ResponseEntity containing the created OrderDto
     */
    @PostMapping("/from-cart/{cartId}")
    @Operation(summary = "3. Create order from cart", description = "Create a new order from shopping cart contents")
    public ResponseEntity<OrderDto> createOrderFromCart(@PathVariable Long cartId) {
        return ResponseEntity.ok(mapper.toDto(orderService.createOrderFromCart(cartId)));
    }

    /**
     * Retrieves all orders for a specific user.
     * Returns the complete order history for the user.
     * 
     * @param userId The ID of the user
     * @return List of OrderDto objects representing user's orders
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "4. Get user orders", description = "Retrieve all orders for a specific user")
    public List<OrderDto> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}