package org.utku.shoppingapi.service;

import org.utku.shoppingapi.entity.Cart;

import java.util.List;

/**
 * Service interface for shopping cart management operations.
 * Defines the contract for cart-related business logic including:
 * - Cart CRUD operations
 * - Cart item management (add, update, remove)
 * - Cart clearing and user-specific operations
 */
public interface CartService {
    
    /**
     * Retrieves all shopping carts in the system.
     * 
     * @return List of all carts
     */
    List<Cart> getAllCarts();
    
    /**
     * Retrieves all carts belonging to a specific user.
     * 
     * @param userId The ID of the user
     * @return List of carts owned by the user
     */
    List<Cart> getCartByUserId(Long userId);
    
    /**
     * Finds a specific cart by its ID.
     * 
     * @param id The cart ID
     * @return The cart entity
     */
    Cart findCartById(Long id);
    
    /**
     * Creates a new shopping cart.
     * 
     * @param cart The cart entity to create
     * @return The created cart entity
     */
    Cart createCart(Cart cart);
    
    /**
     * Updates an existing cart.
     * 
     * @param id The ID of the cart to update
     * @param cart Cart object containing updated information
     * @return The updated cart entity
     */
    Cart updateCart(Long id, Cart cart);
    
    /**
     * Deletes a cart by its ID.
     * 
     * @param id The ID of the cart to delete
     */
    void deleteCart(Long id);
    
    /**
     * Adds a product to the shopping cart.
     * If the product already exists, increases the quantity.
     * 
     * @param cartId The ID of the cart
     * @param productId The ID of the product to add
     * @param quantity The quantity to add
     * @return The updated cart entity
     */
    Cart addItemToCart(Long cartId, Long productId, int quantity);
    
    /**
     * Updates the quantity of a specific product in the cart.
     * 
     * @param cartId The ID of the cart
     * @param productId The ID of the product to update
     * @param quantity The new quantity
     * @return The updated cart entity
     */
    Cart updateItemQuantity(Long cartId, Long productId, int quantity);
    
    /**
     * Removes a specific product from the shopping cart.
     * 
     * @param cartId The ID of the cart
     * @param productId The ID of the product to remove
     * @return The updated cart entity
     */
    Cart removeItemFromCart(Long cartId, Long productId);
    
    /**
     * Clears all items from the shopping cart.
     * 
     * @param cartId The ID of the cart to clear
     * @return The cleared cart entity
     */
    Cart clearCart(Long cartId);
}
