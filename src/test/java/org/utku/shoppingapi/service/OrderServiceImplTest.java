package org.utku.shoppingapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.utku.shoppingapi.entity.*;
import org.utku.shoppingapi.exception.InsufficientStockException;
import org.utku.shoppingapi.repository.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

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
}