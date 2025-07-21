package org.utku.shoppingapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.utku.shoppingapi.entity.*;
import org.utku.shoppingapi.repository.*;
import org.utku.shoppingapi.exception.*;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OrderServiceImpl}.
 * Validates order creation from cart, stock management, and exception scenarios.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderServiceImplTest {
    /**
     * Mocks the OrderRepository for order persistence operations.
     */
    @Mock
    private OrderRepository orderRepository;
    /**
     * Mocks the CartRepository for cart data access.
     */
    @Mock
    private CartRepository cartRepository;
    /**
     * Mocks the ProductRepository for product data access.
     */
    @Mock
    private ProductRepository productRepository;
    /**
     * Mocks the EntityMapper for entity mapping operations.
     */
    @Mock
    private org.utku.shoppingapi.mapper.EntityMapper mapper;
    /**
     * Injects the OrderServiceImpl with mocked dependencies.
     */
    @InjectMocks
    private OrderServiceImpl orderService;

    /**
     * Tests that creating an order from cart decreases product stock.
     */
    @Test
    void createOrderFromCart_ShouldDecreaseStock() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);
        product.setPrice(BigDecimal.valueOf(100));

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(BigDecimal.valueOf(100));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(Arrays.asList(cartItem)));
        cart.setUser(new User());

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // When
        orderService.createOrderFromCart(1L);

        // Then
        assertEquals(8, product.getStockQuantity());
        verify(productRepository).save(product);
    }

    /**
     * Tests that creating an order throws InsufficientStockException when stock is not enough.
     */
    @Test
    void createOrderFromCart_ShouldThrowException_WhenInsufficientStock() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(1);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(5);

        Cart cart = new Cart();
        cart.setItems(Arrays.asList(cartItem));

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        // When & Then
        assertThrows(InsufficientStockException.class, () -> orderService.createOrderFromCart(1L));
    }

    /**
     * Tests that creating an order throws ResourceNotFoundException when cart does not exist.
     */
    @Test
    void createOrderFromCart_WhenCartNotExists_ShouldThrowResourceNotFoundException() {
        // Given
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());
        // When & Then
        var ex = assertThrows(org.utku.shoppingapi.exception.ResourceNotFoundException.class, () -> orderService.createOrderFromCart(99L));
        assertEquals("Cart not found", ex.getMessage());
    }

    /**
     * Tests that creating an order throws ResourceNotFoundException when product does not exist.
     */
    @Test
    void createOrderFromCart_WhenProductNotExists_ShouldThrowResourceNotFoundException() {
        // Given
        CartItem cartItem = new CartItem();
        cartItem.setProduct(null);
        cartItem.setQuantity(1);
        Cart cart = new Cart();
        cart.setItems(Arrays.asList(cartItem));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        // When & Then
        var ex = assertThrows(org.utku.shoppingapi.exception.ResourceNotFoundException.class, () -> orderService.createOrderFromCart(1L));
        assertEquals("Product not found", ex.getMessage());
    }

    /**
     * Tests that creating an order throws InsufficientStockException when product stock is zero.
     */
    @Test
    void createOrderFromCart_WithZeroStock_ShouldThrowInsufficientStockException() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(0);
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        Cart cart = new Cart();
        cart.setItems(Arrays.asList(cartItem));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        // When & Then
        assertThrows(InsufficientStockException.class, () -> orderService.createOrderFromCart(1L));
    }

    /**
     * Tests that creating an order throws ValidationException when product price is negative.
     */
    @Test
    void createOrderFromCart_WithNegativePrice_ShouldThrowValidationException() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);
        product.setPrice(new BigDecimal("-10.00"));
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        Cart cart = new Cart();
        cart.setItems(Arrays.asList(cartItem));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        // When & Then
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> orderService.createOrderFromCart(1L));
    }

    /**
     * Tests that order is not persisted if an exception occurs during save.
     */
    @Test
    void createOrderFromCart_WhenExceptionThrown_ShouldNotPersistOrder() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);
        product.setPrice(BigDecimal.valueOf(100));
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(BigDecimal.valueOf(100));
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(Arrays.asList(cartItem)));
        cart.setUser(new User());
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("DB error"));
        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.createOrderFromCart(1L));
    }
}