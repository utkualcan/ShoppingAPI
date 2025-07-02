package org.utku.shoppingapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.utku.shoppingapi.entity.Cart;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.service.CartService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;
    
    @MockBean
    private EntityMapper entityMapper;

    @Test
    void getCartById_ShouldReturnCart() throws Exception {
        Cart cart = new Cart();
        cart.setId(1L);
        
        when(cartService.findCartById(1L)).thenReturn(cart);

        mockMvc.perform(get("/api/cart/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void clearCart_ShouldReturnClearedCart() throws Exception {
        Cart cart = new Cart();
        cart.setId(1L);
        
        when(cartService.clearCart(1L)).thenReturn(cart);

        mockMvc.perform(delete("/api/cart/1/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}