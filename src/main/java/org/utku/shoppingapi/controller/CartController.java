package org.utku.shoppingapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.utku.shoppingapi.dto.CartDto;
import org.utku.shoppingapi.dto.SimpleCartDto;
import org.utku.shoppingapi.dto.request.AddToCartRequest;
import org.utku.shoppingapi.dto.request.CreateCartRequest;
import org.utku.shoppingapi.dto.request.UpdateCartRequest;
import org.utku.shoppingapi.dto.request.UpdateQuantityRequest;
import org.utku.shoppingapi.entity.Cart;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing shopping cart operations.
 * This controller handles all HTTP requests related to shopping cart management including:
 * - Creating and managing shopping carts
 * - Adding, updating, and removing items from carts
 * - Retrieving cart information
 * - Clearing cart contents
 * 
 * All endpoints are prefixed with '/api/cart' and work with CartDto objects.
 */
@RestController
@RequestMapping("/api/cart")
@Tag(name = "3. Shopping Cart Management", description = "API for managing shopping carts and cart items")
public class CartController {

    private final CartService cartService;
    private final EntityMapper mapper;

    /**
     * Constructor for dependency injection.
     * 
     * @param cartService Service layer for cart business logic
     * @param mapper Entity to DTO mapper for data transformation
     */
    public CartController(CartService cartService, EntityMapper mapper) {
        this.cartService = cartService;
        this.mapper = mapper;
    }

    /**
     * Retrieves all shopping carts in the system.
     * This endpoint is typically used for administrative purposes.
     * 
     * @return List of CartDto objects representing all carts
     */
    @GetMapping
    @Operation(summary = "1. Get all carts", description = "Retrieve all shopping carts in the system")
    public List<CartDto> getAllCarts() {
        return cartService.getAllCarts().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new shopping cart.
     * 
     * @param request Cart creation request containing user ID and optional session ID
     * @return ResponseEntity containing the newly created CartDto
     */
    @PostMapping
    @Operation(summary = "2. Create new cart", description = "Create a new shopping cart")
    public ResponseEntity<CartDto> createCart(@Valid @RequestBody CreateCartRequest request) {
        Cart cart = mapper.toEntity(request);
        return ResponseEntity.ok(mapper.toDto(cartService.createCart(cart)));
    }

    /**
     * Clears all items from a shopping cart.
     * Removes all cart items while keeping the cart itself.
     * 
     * @param cartId The unique identifier of the cart to clear
     * @return ResponseEntity containing the cleared CartDto
     */
    @DeleteMapping("/{cartId}/clear")
    @Operation(summary = "3. Clear cart", description = "Remove all items from a shopping cart")
    public ResponseEntity<CartDto> clearCart(@PathVariable Long cartId) {
        return ResponseEntity.ok(mapper.toDto(cartService.clearCart(cartId)));
    }

    /**
     * Adds a product to the shopping cart.
     * If the product already exists in the cart, increases the quantity.
     * 
     * @param cartId The unique identifier of the cart
     * @param request Request containing product ID and quantity to add
     * @return ResponseEntity containing the updated CartDto
     */
    @PostMapping("/{cartId}/items")
    @Operation(summary = "4. Add item to cart", description = "Add a product to the shopping cart with specified quantity")
    public ResponseEntity<CartDto> addItemToCart(@PathVariable Long cartId, @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(mapper.toDto(cartService.addItemToCart(cartId, request.getProductId(), request.getQuantity())));
    }

    /**
     * Updates the quantity of a specific product in the cart.
     * 
     * @param cartId The unique identifier of the cart
     * @param productId The unique identifier of the product to update
     * @param request Request containing the new quantity
     * @return ResponseEntity containing the updated CartDto
     */
    @PutMapping("/{cartId}/items/{productId}")
    @Operation(summary = "5. Update item quantity", description = "Update the quantity of a specific product in the cart")
    public ResponseEntity<CartDto> updateItemQuantity(@PathVariable Long cartId, @PathVariable Long productId, @Valid @RequestBody UpdateQuantityRequest request) {
        return ResponseEntity.ok(mapper.toDto(cartService.updateItemQuantity(cartId, productId, request.getQuantity())));
    }

    /**
     * Removes a specific product from the shopping cart.
     * 
     * @param cartId The unique identifier of the cart
     * @param productId The unique identifier of the product to remove
     * @return ResponseEntity containing the updated CartDto
     */
    @DeleteMapping("/{cartId}/items/{productId}")
    @Operation(summary = "6. Remove item from cart", description = "Remove a specific product from the shopping cart")
    public ResponseEntity<CartDto> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        return ResponseEntity.ok(mapper.toDto(cartService.removeItemFromCart(cartId, productId)));
    }

    /**
     * Retrieves a specific shopping cart by its ID.
     * 
     * @param id The unique identifier of the cart
     * @return ResponseEntity containing the CartDto if found
     */
    @GetMapping("/{id}")
    @Operation(summary = "7. Get cart by ID", description = "Retrieve a specific shopping cart by its unique identifier")
    public ResponseEntity<CartDto> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(cartService.findCartById(id)));
    }

    /**
     * Updates an existing shopping cart.
     * 
     * @param id The unique identifier of the cart to update
     * @param request Cart update request containing fields to update
     * @return ResponseEntity containing the updated CartDto
     */
    @PutMapping("/{id}")
    @Operation(summary = "8. Update cart", description = "Update an existing shopping cart")
    public ResponseEntity<CartDto> updateCart(@PathVariable Long id, @Valid @RequestBody UpdateCartRequest request) {
        Cart existingCart = cartService.findCartById(id);
        mapper.updateEntityFromRequest(existingCart, request);
        return ResponseEntity.ok(mapper.toDto(cartService.updateCart(id, existingCart)));
    }

    /**
     * Deletes a shopping cart.
     * Permanently removes the cart and all its items from the system.
     * 
     * @param id The unique identifier of the cart to delete
     * @return ResponseEntity with 204 No Content status indicating successful deletion
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "9. Delete cart", description = "Permanently delete a shopping cart")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a specific cart in simplified format (less IDs, more readable).
     * 
     * @param id The ID of the cart to retrieve
     * @return ResponseEntity containing the SimpleCartDto
     */
    @GetMapping("/{id}/simple")
    @Operation(summary = "10. Get cart (simple format)", description = "Retrieve cart with simplified structure and fewer ID references")
    public ResponseEntity<SimpleCartDto> getSimpleCartById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toSimpleDto(cartService.findCartById(id)));
    }

    /**
     * Retrieves shopping carts associated with a specific user.
     * 
     * @param userId The unique identifier of the user
     * @return List of CartDto objects belonging to the specified user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "11. Get carts by user ID", description = "Retrieve all shopping carts for a specific user")
    public List<CartDto> getCartByUserId(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    }