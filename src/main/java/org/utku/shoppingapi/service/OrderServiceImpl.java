package org.utku.shoppingapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.entity.*;
import org.utku.shoppingapi.repository.*;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.exception.InsufficientStockException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Order createOrderFromCart(Long cartId) {
        logger.info("Creating order from cart with ID: {}", cartId);
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException(AppConstants.CART_IS_EMPTY);
        }

        // Stok kontrolü ve azaltma
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(AppConstants.INSUFFICIENT_STOCK + product.getName());
            }
            // Direct stock management to avoid circular dependency
            product.decreaseStock(cartItem.getQuantity());
            productRepository.save(product);
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setTotalAmount(cart.getTotalPrice());

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setUnitPrice(cartItem.getUnitPrice());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with ID: {}, Total: {}", savedOrder.getId(), savedOrder.getTotalAmount());

        cart.getItems().clear();
        cartRepository.save(cart);
        logger.info("Cart cleared after order creation");

        return savedOrder;
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ORDER_NOT_FOUND + orderId));
    }
}