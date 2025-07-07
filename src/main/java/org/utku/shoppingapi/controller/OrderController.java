package org.utku.shoppingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.dto.OrderDto;
import org.utku.shoppingapi.dto.SimpleOrderDto;
import org.utku.shoppingapi.service.OrderService;

import java.util.List;

/**
 * REST Controller for managing order operations.
 * Security is handled at the service layer using @PreAuthorize.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "4. Order Management", description = "API for managing orders and order history")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * [ADMIN] Retrieves all orders in the system.
     */
    @GetMapping
    @Operation(summary = "1. [ADMIN] Get all orders", description = "Retrieve a list of all orders in the system.")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAllOrders());
    }

    /**
     * [ADMIN / USER] Retrieves a specific order by its ID.
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "2. Get order by ID", description = "Retrieve a specific order. Admins can access any order, users only their own.")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.findOrderById(orderId));
    }

    /**
     * [ADMIN / USER] Retrieves a specific order in a simplified format.
     */
    @GetMapping("/{orderId}/simple")
    @Operation(summary = "3. Get order (simple format)", description = "Retrieve a simplified version of an order.")
    public ResponseEntity<SimpleOrderDto> getSimpleOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.findSimpleOrderById(orderId));
    }

    /**
     * [USER] Creates a new order from the contents of a shopping cart.
     */
    @PostMapping("/from-cart/{cartId}")
    @Operation(summary = "4. Create order from cart", description = "Create a new order from a user's shopping cart.")
    public ResponseEntity<OrderDto> createOrderFromCart(@PathVariable Long cartId) {
        return ResponseEntity.ok(orderService.createOrderFromCart(cartId));
    }

    /**
     * [ADMIN / USER] Retrieves all orders for a specific user.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "5. Get user's orders", description = "Retrieve all orders for a specific user. Admins can access any user's orders.")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.findOrdersByUserId(userId));
    }
}