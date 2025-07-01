package org.utku.shoppingapi.service;

import org.utku.shoppingapi.entity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> getAllCarts();
    List<Cart> getCartByUserId(Long userId);
    Cart createCart(Cart cart);
    Cart updateCart(Long id, Cart cart);
    void deleteCart(Long id);
    Cart addItemToCart(Long cartId, Long productId, int quantity);
    Cart updateItemQuantity(Long cartId, Long productId, int quantity);
    Cart removeItemFromCart(Long cartId, Long productId);
}
