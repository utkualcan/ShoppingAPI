package org.utku.shoppingapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.utku.shoppingapi.dto.CartDto;
import org.utku.shoppingapi.entity.Cart;
import org.utku.shoppingapi.service.CartService;
import org.utku.shoppingapi.mapper.EntityMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link CartController}.
 * Validates cart retrieval and clearing operations, including error scenarios.
 */
@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {
    /**
     * Injects the MockMvc for simulating HTTP requests.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mocks the CartService for cart business logic operations.
     */
    @MockBean
    private CartService cartService;
    
    /**
     * Mocks the EntityMapper for DTO mapping operations.
     */
    @MockBean
    private EntityMapper entityMapper;

    /**
     * Injects the ObjectMapper for JSON serialization/deserialization.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Simulated JWT token for authorization header in tests.
     */
    private final String jwtToken = "Bearer test.jwt.token";

    /**
     * Tests that getCartById returns the cart DTO when the cart exists.
     */
    @Test
    void getCartById_ShouldReturnCart() throws Exception {
        Cart cart = new Cart();
        cart.setId(1L);
        CartDto cartDto = new CartDto();
        cartDto.setId(1L);
        when(cartService.findCartById(1L)).thenReturn(cartDto);

        mockMvc.perform(get("/api/cart/1")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    /**
     * Tests that clearCart returns the cleared cart DTO.
     */
    @Test
    void clearCart_ShouldReturnClearedCart() throws Exception {
        Cart cart = new Cart();
        cart.setId(1L);
        CartDto cartDto = new CartDto();
        cartDto.setId(1L);
        when(cartService.clearCart(1L)).thenReturn(cartDto);

        mockMvc.perform(delete("/api/cart/1/clear")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    /**
     * Tests that getCartById returns not found when the cart does not exist.
     */
    @Test
    void getCartById_WhenCartNotExists_ShouldReturnNotFound() throws Exception {
        when(cartService.findCartById(99L)).thenThrow(new org.utku.shoppingapi.exception.ResourceNotFoundException("Cart not found"));
        mockMvc.perform(get("/api/cart/99")
                .header("Authorization", jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cart not found"));
    }

    /**
     * Tests that clearCart returns an empty cart DTO when the cart is already empty.
     */
    @Test
    void clearCart_WhenCartIsEmpty_ShouldReturnEmptyCart() throws Exception {
        CartDto emptyCartDto = new CartDto();
        emptyCartDto.setId(2L);
        emptyCartDto.setItems(java.util.Collections.emptyList());
        when(cartService.clearCart(2L)).thenReturn(emptyCartDto);
        mockMvc.perform(delete("/api/cart/2/clear")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.items").isEmpty());
    }
}