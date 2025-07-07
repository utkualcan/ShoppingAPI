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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CartService.
 * Handles all business logic and security for shopping carts.
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final EntityMapper mapper;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, EntityMapper mapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<CartDto> findAllCarts() {
        return cartRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public CartDto findCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public SimpleCartDto findSimpleCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .map(mapper::toSimpleDto)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public List<CartDto> findCartsByUserId(Long userId) {
        return cartRepository.findByUserId(userId).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public CartDto addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = findCartEntityById(cartId);
        Product product = findProductEntityById(productId);

        CartItem existingItem = findItemByProduct(cart, product);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }
        return mapper.toDto(cartRepository.save(cart));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public CartDto updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = findCartEntityById(cartId);
        Product product = findProductEntityById(productId);

        CartItem item = findItemByProduct(cart, product);
        if (item == null) {
            throw new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND_IN_CART);
        }
        item.setQuantity(quantity);
        return mapper.toDto(cartRepository.save(cart));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public CartDto removeItemFromCart(Long cartId, Long productId) {
        Cart cart = findCartEntityById(cartId);
        Product product = findProductEntityById(productId);

        CartItem item = findItemByProduct(cart, product);
        if (item != null) {
            cart.getItems().remove(item);
            return mapper.toDto(cartRepository.save(cart));
        }
        throw new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND_IN_CART);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartOwner(#cartId)")
    public CartDto clearCart(Long cartId) {
        Cart cart = findCartEntityById(cartId);
        cart.getItems().clear();
        return mapper.toDto(cartRepository.save(cart));
    }

    // --- Helper Methods ---
    private Cart findCartEntityById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
    }

    private Product findProductEntityById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
    }

    private CartItem findItemByProduct(Cart cart, Product product) {
        return cart.getItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .orElse(null);
    }
}