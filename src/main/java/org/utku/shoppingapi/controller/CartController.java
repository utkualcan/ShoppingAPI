package org.utku.shoppingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.dto.CartDto;
import org.utku.shoppingapi.dto.SimpleCartDto;
import org.utku.shoppingapi.dto.request.AddToCartRequest;
import org.utku.shoppingapi.dto.request.UpdateQuantityRequest;
import org.utku.shoppingapi.service.CartService;

import java.util.List;

/**
 * REST Controller for managing shopping cart operations.
 * Security is handled at the service layer.
 */
@RestController
@RequestMapping("/api/cart")
@Tag(name = "3. Shopping Cart Management", description = "API for managing shopping carts and items")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Retrieves all shopping carts in the system. Only accessible by ADMIN users.
     *
     * @return List of CartDto objects representing all carts
     */
    @GetMapping
    @Operation(summary = "1. [ADMIN] Get all carts")
    public ResponseEntity<List<CartDto>> getAllCarts() {
        return ResponseEntity.ok(cartService.findAllCarts());
    }

    /**
     * Retrieves a cart by its unique identifier.
     *
     * @param id Unique identifier of the cart
     * @return CartDto object representing the cart
     */
    @GetMapping("/{id}")
    @Operation(summary = "2. Get cart by ID")
    public ResponseEntity<CartDto> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.findCartById(id));
    }

    /**
     * Retrieves a simplified version of the cart by its ID.
     *
     * @param id Unique identifier of the cart
     * @return SimpleCartDto object representing the simplified cart
     */
    @GetMapping("/{id}/simple")
    @Operation(summary = "3. Get simple cart by ID")
    public ResponseEntity<SimpleCartDto> getSimpleCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.findSimpleCartById(id));
    }

    /**
     * Retrieves all carts belonging to a specific user.
     *
     * @param userId Unique identifier of the user
     * @return List of CartDto objects representing the user's carts
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "4. Get carts by User ID")
    public ResponseEntity<List<CartDto>> getCartByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.findCartsByUserId(userId));
    }

    /**
     * Adds a product to the user's cart with the specified quantity.
     *
     * @param userId Unique identifier of the user
     * @param request AddToCartRequest containing productId and quantity
     * @return CartDto object representing the updated cart
     */
    @PostMapping("/user/{userId}/items")
    @Operation(summary = "5. Add item to cart")
    public ResponseEntity<CartDto> addItemToCart(@PathVariable Long userId, @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, request.getProductId(), request.getQuantity()));
    }

    /**
     * Updates the quantity of a specific product in the cart.
     *
     * @param cartId Unique identifier of the cart
     * @param productId Unique identifier of the product
     * @param request UpdateQuantityRequest containing new quantity
     * @return CartDto object representing the updated cart
     */
    @PutMapping("/{cartId}/items/{productId}")
    @Operation(summary = "6. Update item quantity")
    public ResponseEntity<CartDto> updateItemQuantity(@PathVariable Long cartId, @PathVariable Long productId, @Valid @RequestBody UpdateQuantityRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(cartId, productId, request.getQuantity()));
    }

    /**
     * Removes a product from the cart.
     *
     * @param cartId Unique identifier of the cart
     * @param productId Unique identifier of the product
     * @return CartDto object representing the updated cart
     */
    @DeleteMapping("/{cartId}/items/{productId}")
    @Operation(summary = "7. Remove item from cart")
    public ResponseEntity<CartDto> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(cartId, productId));
    }

    /**
     * Clears all items from the specified cart.
     *
     * @param cartId Unique identifier of the cart
     * @return CartDto object representing the emptied cart
     */
    @DeleteMapping("/{cartId}/clear")
    @Operation(summary = "8. Clear all items from cart")
    public ResponseEntity<CartDto> clearCart(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.clearCart(cartId));
    }
}