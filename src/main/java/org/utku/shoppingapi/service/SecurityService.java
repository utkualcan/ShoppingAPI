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

    public boolean isOrderOwner(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) return false;
        return orderOpt.get().getUser().getId().equals(getCurrentUserId());
    }

    public boolean isCartOwner(Long cartId) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) return false;
        return cartOpt.get().getUser().getId().equals(getCurrentUserId());
    }

    public boolean isFavoriteOwner(Long favoriteId) {
        Optional<Favorite> favoriteOpt = favoriteRepository.findById(favoriteId);
        if (favoriteOpt.isEmpty()) return false;
        return favoriteOpt.get().getUser().getId().equals(getCurrentUserId());
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getId();
        }
        return null;
    }
}