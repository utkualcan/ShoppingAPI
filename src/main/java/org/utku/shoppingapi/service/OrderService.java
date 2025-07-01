package org.utku.shoppingapi.service;

import org.utku.shoppingapi.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrderFromCart(Long cartId);
    List<Order> getOrdersByUserId(Long userId);
    Order getOrderById(Long orderId);
}