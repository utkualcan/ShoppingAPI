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

/**
 * Implementation of OrderService.
 * Handles all business logic and security for order management operations.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    /**
     * Logger for logging order service operations and errors.
     */
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    /**
     * Repository for order data access.
     */
    private final OrderRepository orderRepository;
    /**
     * Repository for cart data access.
     */
    private final CartRepository cartRepository;
    /**
     * Repository for product data access.
     */
    private final ProductRepository productRepository;
    /**
     * Mapper for converting entities to DTOs.
     */
    private final EntityMapper mapper;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, ProductRepository productRepository, EntityMapper mapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    /**
     * Creates a new order from the contents of a shopping cart. Access is restricted to the cart owner.
     * @param cartId Cart ID
     * @return Created OrderDto
     */
    @Override
    @Transactional
    @PreAuthorize("@securityService.isCartOwner(#cartId)")
    public OrderDto createOrderFromCart(Long cartId) {
        logger.info("Creating order from cart ID: {}", cartId);
        // Find cart and validate items
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException(AppConstants.CART_IS_EMPTY);
        }
        // Check stock for each item and update product stock
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new ResourceNotFoundException("Product not found");
            }
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(AppConstants.INSUFFICIENT_STOCK + product.getName());
            }
            product.decreaseStock(cartItem.getQuantity());
            productRepository.save(product);
        }
        // Create order and map cart items to order items
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
        // Clear cart after order creation
        cart.getItems().clear();
        cartRepository.save(cart);
        return mapper.toDto(savedOrder);
    }

    /**
     * Retrieves all orders in the system. Only accessible by ADMIN users.
     * @return List of OrderDto objects
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDto> findAllOrders() {
        // Return all orders in the system (ADMIN only)
        return orderRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Retrieves all orders for a specific user. Access is restricted to the user or ADMIN.
     * @param userId User ID
     * @return List of OrderDto objects
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public List<OrderDto> findOrdersByUserId(Long userId) {
        // Return all orders for a specific user
        return orderRepository.findByUserId(userId).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Finds a specific order by its ID. Access is restricted to the order owner or ADMIN.
     * @param orderId Order ID
     * @return OrderDto object
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderOwner(#orderId)")
    public OrderDto findOrderById(Long orderId) {
        // Find order by ID and map to DTO
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ORDER_NOT_FOUND + orderId));
        return mapper.toDto(order);
    }

    /**
     * Finds a specific order by its ID and returns it in a simplified format. Access is restricted to the order owner or ADMIN.
     * @param orderId Order ID
     * @return SimpleOrderDto object
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderOwner(#orderId)")
    public SimpleOrderDto findSimpleOrderById(Long orderId) {
        // Find order by ID and map to simple DTO
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ORDER_NOT_FOUND + orderId));
        return mapper.toSimpleDto(order);
    }
}