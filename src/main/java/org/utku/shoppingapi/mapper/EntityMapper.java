package org.utku.shoppingapi.mapper;

import org.springframework.stereotype.Component;
import org.utku.shoppingapi.dto.*;
import org.utku.shoppingapi.dto.request.*;
import org.utku.shoppingapi.entity.*;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Centralized mapper for converting between entities and DTOs.
 * This class handles all transformations to maintain consistency and reduce code duplication.
 * 
 * Provides mapping methods for:
 * - Entity to DTO conversions for API responses
 * - Request DTO to Entity conversions for data persistence
 * - Simple DTO conversions with reduced complexity
 * - Partial update operations
 * 
 * @author Shopping API Team
 * @version 1.0
 * @since 1.0
 */
@Component
public class EntityMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .build();
    }

    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .active(product.getActive())
                .build();
    }

    public CartDto toDto(Cart cart) {
        return CartDto.builder()
                .id(cart.getId())
                .userId(Optional.ofNullable(cart.getUser())
                        .map(User::getId)
                        .orElse(null))
                .items(cart.getItems().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()))
                .totalPrice(cart.getTotalPrice())
                .totalItems(cart.getTotalItems())
                .build();
    }

    public CartItemDto toDto(CartItem item) {
        return CartItemDto.builder()
                .id(item.getId())
                .product(toDto(item.getProduct()))
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(Optional.ofNullable(order.getUser())
                        .map(User::getId)
                        .orElse(null))
                .items(order.getItems().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()))
                .totalAmount(order.getTotalAmount())
                .orderedAt(order.getOrderedAt())
                .build();
    }

    public OrderItemDto toDto(OrderItem item) {
        return OrderItemDto.builder()
                .id(item.getId())
                .product(toDto(item.getProduct()))
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public FavoriteDto toDto(Favorite favorite) {
        return FavoriteDto.builder()
                .id(favorite.getId())
                .userId(Optional.ofNullable(favorite.getUser())
                        .map(User::getId)
                        .orElse(null))
                .product(toDto(favorite.getProduct()))
                .createdAt(favorite.getCreatedAt())
                .build();
    }
    
    /**
     * Converts Order entity to simplified DTO with reduced ID complexity.
     * 
     * @param order The order entity
     * @return SimpleOrderDto with essential information only
     */
    public SimpleOrderDto toSimpleDto(Order order) {
        return SimpleOrderDto.builder()
                .orderId(order.getId())
                .customerName(Optional.ofNullable(order.getUser())
                        .map(user -> user.getFirstName() + " " + user.getLastName())
                        .orElse("Unknown Customer"))
                .items(order.getItems().stream()
                        .map(this::toSimpleDto)
                        .collect(Collectors.toList()))
                .totalAmount(order.getTotalAmount())
                .orderedAt(order.getOrderedAt())
                .totalItems(order.getItems().size())
                .build();
    }
    
    /**
     * Converts OrderItem entity to simplified DTO.
     * 
     * @param item The order item entity
     * @return SimpleOrderItemDto with essential product information
     */
    public SimpleOrderItemDto toSimpleDto(OrderItem item) {
        return SimpleOrderItemDto.builder()
                .productName(item.getProduct().getName())
                .category(item.getProduct().getCategory())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
    
    /**
     * Converts Cart entity to simplified DTO with reduced ID complexity.
     * 
     * @param cart The cart entity
     * @return SimpleCartDto with essential information only
     */
    public SimpleCartDto toSimpleDto(Cart cart) {
        return SimpleCartDto.builder()
                .cartId(cart.getId())
                .customerName(Optional.ofNullable(cart.getUser())
                        .map(user -> user.getFirstName() + " " + user.getLastName())
                        .orElse("Guest Customer"))
                .items(cart.getItems().stream()
                        .map(this::toSimpleDto)
                        .collect(Collectors.toList()))
                .totalPrice(cart.getTotalPrice())
                .totalItems(cart.getTotalItems())
                .uniqueProducts(cart.getItems().size())
                .build();
    }
    
    /**
     * Converts CartItem entity to simplified DTO.
     * 
     * @param item The cart item entity
     * @return SimpleCartItemDto with essential product information
     */
    public SimpleCartItemDto toSimpleDto(CartItem item) {
        String stockStatus = item.getProduct().getStockQuantity() > 10 ? "In Stock" :
                           item.getProduct().getStockQuantity() > 0 ? "Low Stock" : "Out of Stock";
                           
        return SimpleCartItemDto.builder()
                .productName(item.getProduct().getName())
                .category(item.getProduct().getCategory())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .stockStatus(stockStatus)
                .build();
    }

    // Request to Entity mappings
    
    /**
     * Converts CreateUserRequest to User entity.
     * 
     * @param request The user creation request
     * @return User entity with populated fields
     */
    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        return user;
    }

    /**
     * Updates User entity from UpdateUserRequest.
     * Only updates non-null fields to support partial updates.
     * 
     * @param user The existing user entity
     * @param request The update request
     */
    public void updateEntityFromRequest(User user, UpdateUserRequest request) {
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPassword(request.getPassword());
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());
    }

    /**
     * Converts CreateProductRequest to Product entity.
     * 
     * @param request The product creation request
     * @return Product entity with populated fields
     */
    public Product toEntity(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setActive(true); // New products are active by default
        return product;
    }

    /**
     * Converts CreateCartRequest to Cart entity.
     * 
     * @param request The cart creation request
     * @return Cart entity with populated fields
     */
    public Cart toEntity(CreateCartRequest request) {
        Cart cart = new Cart();
        if (request.getUserId() != null) {
            User user = new User();
            user.setId(request.getUserId());
            cart.setUser(user);
        }
        cart.setSessionId(request.getSessionId());
        return cart;
    }

    /**
     * Updates Cart entity from UpdateCartRequest.
     * Only updates non-null fields to support partial updates.
     * 
     * @param cart The existing cart entity
     * @param request The update request
     */
    public void updateEntityFromRequest(Cart cart, UpdateCartRequest request) {
        if (request.getUserId() != null) {
            User user = new User();
            user.setId(request.getUserId());
            cart.setUser(user);
        }
        if (request.getSessionId() != null) {
            cart.setSessionId(request.getSessionId());
        }
    }

    /**
     * Updates Product entity from UpdateProductRequest.
     * Only updates non-null fields to support partial updates.
     * 
     * @param product The existing product entity
     * @param request The update request
     */
    public void updateEntityFromRequest(Product product, UpdateProductRequest request) {
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getActive() != null) product.setActive(request.getActive());
    }
    

}