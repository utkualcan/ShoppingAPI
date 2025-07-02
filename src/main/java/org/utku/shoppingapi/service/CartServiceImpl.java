package org.utku.shoppingapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.entity.Cart;
import org.utku.shoppingapi.entity.CartItem;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.repository.CartRepository;
import org.utku.shoppingapi.repository.ProductRepository;
import org.utku.shoppingapi.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Implementation of CartService interface.
 * This service class handles all business logic related to shopping cart management including:
 * - CRUD operations for carts
 * - Cart item management (add, update, remove)
 * - Integration with CartRepository and ProductRepository
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * Constructor for dependency injection.
     * 
     * @param cartRepository Repository for cart data access
     * @param productRepository Repository for product data access
     */
    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all shopping carts in the system.
     * 
     * @return List of all carts
     */
    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    /**
     * Retrieves all carts associated with a specific user.
     * 
     * @param userId The ID of the user
     * @return List of carts belonging to the user
     */
    @Override
    public List<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    /**
     * Finds a specific cart by its ID.
     * 
     * @param id The cart ID
     * @return The cart entity
     * @throws ResourceNotFoundException if cart is not found
     */
    @Override
    public Cart findCartById(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + id));
    }

    /**
     * Creates a new shopping cart.
     * 
     * @param cart The cart entity to create
     * @return The created cart entity
     */
    @Override
    public Cart createCart(Cart cart) {
        return cartRepository.save(cart);
    }

    /**
     * Updates an existing cart.
     * 
     * @param id The ID of the cart to update
     * @param cart Cart object containing updated information
     * @return The updated cart entity
     * @throws ResourceNotFoundException if cart is not found
     */
    @Override
    public Cart updateCart(Long id, Cart cart) {
        if (!cartRepository.existsById(id)) {
            throw new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + id);
        }
        cart.setId(id);
        return cartRepository.save(cart);
    }

    /**
     * Deletes a cart by its ID.
     * 
     * @param id The ID of the cart to delete
     */
    @Override
    public void deleteCart(Long id) {
        if (!cartRepository.existsById(id)) {
            throw new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + id);
        }
        cartRepository.deleteById(id);
    }

    /**
     * Adds a product to the shopping cart.
     * If the product already exists in the cart, increases the quantity.
     * Otherwise, creates a new cart item.
     * 
     * @param cartId The ID of the cart
     * @param productId The ID of the product to add
     * @param quantity The quantity to add
     * @return The updated cart entity
     * @throws ResourceNotFoundException if cart or product is not found
     */
    @Override
    public Cart addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
        
        CartItem existingItem = findItemByProduct(cart, product);
        if (existingItem != null) {
            // Product already exists in cart, increase quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Create new cart item
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }
        return cartRepository.save(cart);
    }

    /**
     * Updates the quantity of a specific product in the cart.
     * 
     * @param cartId The ID of the cart
     * @param productId The ID of the product to update
     * @param quantity The new quantity
     * @return The updated cart entity
     * @throws ResourceNotFoundException if cart, product, or cart item is not found
     */
    @Override
    public Cart updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
        
        CartItem item = findItemByProduct(cart, product);
        if (item == null) {
            throw new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND_IN_CART);
        }
        
        item.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    /**
     * Removes a specific product from the shopping cart.
     * 
     * @param cartId The ID of the cart
     * @param productId The ID of the product to remove
     * @return The updated cart entity
     * @throws ResourceNotFoundException if cart, product, or cart item is not found
     */
    @Override
    public Cart removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
        
        CartItem item = findItemByProduct(cart, product);
        if (item != null) {
            cart.getItems().remove(item);
            return cartRepository.save(cart);
        }
        
        throw new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND_IN_CART);
    }

    /**
     * Clears all items from the shopping cart.
     * 
     * @param cartId The ID of the cart to clear
     * @return The cleared cart entity
     * @throws ResourceNotFoundException if cart is not found
     */
    @Override
    public Cart clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
        cart.getItems().clear();
        return cartRepository.save(cart);
    }
    
    /**
     * Helper method to find a cart item by product.
     * This replaces the business logic that was in the Cart entity.
     * 
     * @param cart The cart to search in
     * @param product The product to find
     * @return CartItem if found, null otherwise
     */
    private CartItem findItemByProduct(Cart cart, Product product) {
        return cart.getItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .orElse(null);
    }
}