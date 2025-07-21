package org.utku.shoppingapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.dto.CartDto;
import org.utku.shoppingapi.dto.SimpleCartDto;
import org.utku.shoppingapi.entity.Cart;
import org.utku.shoppingapi.entity.CartItem;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.repository.CartRepository;
import org.utku.shoppingapi.repository.ProductRepository;
import org.utku.shoppingapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CartService.
 * Handles all business logic and security for shopping carts.
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    /**
     * Logger for logging cart service operations and errors.
     */
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    /**
     * Repository for cart data access.
     */
    private final CartRepository cartRepository;
    /**
     * Repository for product data access.
     */
    private final ProductRepository productRepository;
    /**
     * Repository for user data access.
     */
    private final UserRepository userRepository;
    /**
     * Mapper for converting entities to DTOs.
     */
    private final EntityMapper mapper;

    /**
     * Constructs a CartServiceImpl with required dependencies.
     * @param cartRepository Cart repository
     * @param productRepository Product repository
     * @param userRepository User repository
     * @param mapper Entity to DTO mapper
     */
    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository, EntityMapper mapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves all shopping carts in the system. Only accessible by ADMIN users.
     * @return List of CartDto objects
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<CartDto> findAllCarts() {
        // Return all carts in the system (ADMIN only)
        return cartRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Retrieves a shopping cart by its ID. Access is restricted to the cart owner or ADMIN.
     * @param cartId Cart ID
     * @return CartDto object
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public CartDto findCartById(Long cartId) {
        // Find cart by ID and map to DTO
        return cartRepository.findById(cartId)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
    }

    /**
     * Retrieves a simplified version of a shopping cart by its ID. Access is restricted to the cart owner or ADMIN.
     * @param cartId Cart ID
     * @return SimpleCartDto object
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public SimpleCartDto findSimpleCartById(Long cartId) {
        // Find cart by ID and map to simple DTO
        return cartRepository.findById(cartId)
                .map(mapper::toSimpleDto)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
    }

    /**
     * Retrieves all carts belonging to a specific user. Access is restricted to the user or ADMIN.
     * @param userId User ID
     * @return List of CartDto objects
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public List<CartDto> findCartsByUserId(Long userId) {
        // Return all carts for a specific user
        return cartRepository.findByUserId(userId).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Adds an item to the specified cart. Access is restricted to the cart owner or ADMIN.
     * @param userId User ID
     * @param productId Product ID
     * @param quantity Quantity to add
     * @return Updated CartDto object
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public CartDto addItemToCart(Long userId, Long productId, int quantity) {
        // Add item to user's cart, create cart if not exists
        Cart cart = cartRepository.findByUserId(userId).stream()
                .findFirst()
                .orElseGet(() -> createCartForUser(userId));

        Product product = findProductEntityById(productId);

        CartItem existingItem = findItemByProduct(cart, product);
        if (existingItem != null) {
            // Increase quantity if item exists
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        return mapper.toDto(cartRepository.save(cart));
    }

    /**
     * Updates the quantity of an item in the specified cart. Access is restricted to the cart owner or ADMIN.
     * @param cartId Cart ID
     * @param productId Product ID
     * @param quantity New quantity
     * @return Updated CartDto object
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public CartDto updateItemQuantity(Long cartId, Long productId, int quantity) {
        // Update quantity of a specific item in cart
        Cart cart = findCartEntityById(cartId);
        Product product = findProductEntityById(productId);

        CartItem item = findItemByProduct(cart, product);
        if (item == null) {
            throw new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND_IN_CART);
        }
        item.setQuantity(quantity);
        return mapper.toDto(cartRepository.save(cart));
    }

    /**
     * Removes an item from the specified cart. Access is restricted to the cart owner or ADMIN.
     * @param cartId Cart ID
     * @param productId Product ID
     * @return Updated CartDto object
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public CartDto removeItemFromCart(Long cartId, Long productId) {
        // Remove item from cart
        Cart cart = findCartEntityById(cartId);
        Product product = findProductEntityById(productId);

        CartItem item = findItemByProduct(cart, product);
        if (item != null) {
            cart.getItems().remove(item);
            return mapper.toDto(cartRepository.save(cart));
        }
        throw new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND_IN_CART);
    }

    /**
     * Clears all items from the specified cart. Access is restricted to the cart owner or ADMIN.
     * @param cartId Cart ID
     * @return Updated CartDto object
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public CartDto clearCart(Long cartId) {
        // Remove all items from cart
        Cart cart = findCartEntityById(cartId);
        cart.getItems().clear();
        return mapper.toDto(cartRepository.save(cart));
    }

    // --- Helper Methods ---
    /**
     * Finds a cart entity by its ID.
     * @param cartId Cart ID
     * @return Cart entity
     * @throws ResourceNotFoundException if cart not found
     */
    private Cart findCartEntityById(Long cartId) {
        // Find cart entity by ID
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
    }

    /**
     * Finds a product entity by its ID.
     * @param productId Product ID
     * @return Product entity
     * @throws ResourceNotFoundException if product not found
     */
    private Product findProductEntityById(Long productId) {
        // Find product entity by ID
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
    }

    /**
     * Finds a cart item by product in the cart.
     * @param cart Cart entity
     * @param product Product entity
     * @return CartItem if found, null otherwise
     */
    private CartItem findItemByProduct(Cart cart, Product product) {
        // Find cart item by product
        return cart.getItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates a new cart for the specified user.
     * @param userId User ID
     * @return Newly created Cart entity
     */
    private Cart createCartForUser(Long userId) {
        // Create new cart for user
        Cart cart = new Cart();
        cart.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + userId)));
        return cartRepository.save(cart);
    }
}