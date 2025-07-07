package org.utku.shoppingapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.dto.OrderDto;
import org.utku.shoppingapi.dto.SimpleOrderDto;
import org.utku.shoppingapi.entity.*;
import org.utku.shoppingapi.exception.InsufficientStockException;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final EntityMapper mapper;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, ProductRepository productRepository, EntityMapper mapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    // Güvenlik kuralı, var olmayan sepetler için çökmemesi amacıyla @securityService kullanacak şekilde güncellendi.
    @PreAuthorize("@securityService.isCartOwner(#cartId)")
    public OrderDto createOrderFromCart(Long cartId) {
        logger.info("Creating order from cart ID: {}", cartId);
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND + cartId));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException(AppConstants.CART_IS_EMPTY);
        }

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(AppConstants.INSUFFICIENT_STOCK + product.getName());
            }
            product.decreaseStock(cartItem.getQuantity());
            productRepository.save(product);
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setTotalAmount(cart.getTotalPrice());
        order.setItems(cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setOrder(order);
            return orderItem;
        }).collect(Collectors.toList()));

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created with ID: {}", savedOrder.getId());
        cart.getItems().clear();
        cartRepository.save(cart);
        return mapper.toDto(savedOrder);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDto> findAllOrders() {
        return orderRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public List<OrderDto> findOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    // Güvenlik kuralı, var olmayan siparişler için çökmemesi amacıyla @securityService kullanacak şekilde güncellendi.
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderOwner(#orderId)")
    public OrderDto findOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ORDER_NOT_FOUND + orderId));
        return mapper.toDto(order);
    }

    @Override
    // Güvenlik kuralı, var olmayan siparişler için çökmemesi amacıyla @securityService kullanacak şekilde güncellendi.
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderOwner(#orderId)")
    public SimpleOrderDto findSimpleOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ORDER_NOT_FOUND + orderId));
        return mapper.toSimpleDto(order);
    }
}