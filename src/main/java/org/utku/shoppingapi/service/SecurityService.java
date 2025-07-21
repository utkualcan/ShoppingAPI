package org.utku.shoppingapi.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.utku.shoppingapi.entity.Cart;
import org.utku.shoppingapi.entity.Favorite;
import org.utku.shoppingapi.entity.Order;
import org.utku.shoppingapi.repository.CartRepository;
import org.utku.shoppingapi.repository.FavoriteRepository;
import org.utku.shoppingapi.repository.OrderRepository;
import org.utku.shoppingapi.security.UserPrincipal;

import java.util.Optional;

/**
 * Service for security-related checks and user context operations.
 * Provides methods to verify resource ownership and retrieve current user ID.
 */
@Service("securityService")
public class SecurityService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final FavoriteRepository favoriteRepository;

    public SecurityService(OrderRepository orderRepository, CartRepository cartRepository, FavoriteRepository favoriteRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.favoriteRepository = favoriteRepository;
    }

    /**
     * Checks if the current authenticated user is the owner of the given order.
     * @param orderId Order ID
     * @return true if current user owns the order, false otherwise
     */
    public boolean isOrderOwner(Long orderId) {
        // Check if current user is owner of the order
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) return false;
        return orderOpt.get().getUser().getId().equals(getCurrentUserId());
    }

    /**
     * Checks if the current authenticated user is the owner of the given cart.
     * @param cartId Cart ID
     * @return true if current user owns the cart, false otherwise
     */
    public boolean isCartOwner(Long cartId) {
        // Check if current user is owner of the cart
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) return false;
        return cartOpt.get().getUser().getId().equals(getCurrentUserId());
    }

    /**
     * Checks if the current authenticated user is the owner of the given favorite entry.
     * @param favoriteId Favorite ID
     * @return true if current user owns the favorite, false otherwise
     */
    public boolean isFavoriteOwner(Long favoriteId) {
        // Check if current user is owner of the favorite entry
        Optional<Favorite> favoriteOpt = favoriteRepository.findById(favoriteId);
        if (favoriteOpt.isEmpty()) return false;
        return favoriteOpt.get().getUser().getId().equals(getCurrentUserId());
    }

    /**
     * Retrieves the ID of the current authenticated user from the security context.
     * @return User ID if authenticated, null otherwise
     */
    public Long getCurrentUserId() {
        // Get current user ID from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getId();
        }
        return null;
    }
}