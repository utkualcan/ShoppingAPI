package org.utku.shoppingapi.service;

import org.utku.shoppingapi.dto.CartDto;
import org.utku.shoppingapi.dto.SimpleCartDto;
import java.util.List;

/**
 * Service interface for shopping cart management.
 * Defines the contract for all cart-related business logic and security.
 */
public interface CartService {

    /**
     * [ADMIN] Retrieves all shopping carts in the system.
     */
    List<CartDto> findAllCarts();

    /**
     * [ADMIN / USER] Retrieves a shopping cart by its ID.
     * Access is restricted to the cart owner or an ADMIN.
     */
    CartDto findCartById(Long cartId);

    /**
     * [ADMIN / USER] Retrieves a simplified version of a shopping cart by its ID.
     * Access is restricted to the cart owner or an ADMIN.
     */
    SimpleCartDto findSimpleCartById(Long cartId);

    /**
     * [ADMIN / USER] Retrieves all carts belonging to a specific user.
     * Access is restricted to the user themselves or an ADMIN.
     */
    List<CartDto> findCartsByUserId(Long userId);

    /**
     * Adds an item to the specified cart.
     * Access is restricted to the cart owner or an ADMIN.
     */
    CartDto addItemToCart(Long cartId, Long productId, int quantity);

    /**
     * Updates the quantity of an item in the specified cart.
     * Access is restricted to the cart owner or an ADMIN.
     */
    CartDto updateItemQuantity(Long cartId, Long productId, int quantity);

    /**
     * Removes an item from the specified cart.
     * Access is restricted to the cart owner or an ADMIN.
     */
    CartDto removeItemFromCart(Long cartId, Long productId);

    /**
     * Clears all items from the specified cart.
     * Access is restricted to the cart owner or an ADMIN.
     */
    CartDto clearCart(Long cartId);
}