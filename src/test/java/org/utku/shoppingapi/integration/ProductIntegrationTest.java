package org.utku.shoppingapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.dto.request.CreateProductRequest;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for product endpoints.
 * Validates product creation, validation, and retrieval scenarios.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {

    /**
     * Injects the MockMvc for simulating HTTP requests.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Injects the ObjectMapper for JSON serialization/deserialization.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Injects the UserRepository for user data access operations.
     */
    @Autowired
    private org.utku.shoppingapi.repository.UserRepository userRepository;

    /**
     * Tests that createProduct with valid data returns the created product.
     */
    @Test
    void createProduct_WithValidData_ShouldReturnCreatedProduct() throws Exception {
        // Register admin user
        var registerRequest = new org.utku.shoppingapi.dto.auth.RegisterRequest();
        registerRequest.setUsername("adminuser");
        registerRequest.setEmail("admin@example.com");
        registerRequest.setPassword("adminpass123");
        registerRequest.setFirstName("Admin");
        registerRequest.setLastName("User");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Add ADMIN role to user
        var userOpt = userRepository.findByUsername("adminuser");
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            user.addRole(org.utku.shoppingapi.entity.Role.ADMIN);
            userRepository.save(user);
        }

        // Login as admin
        var loginRequest = new org.utku.shoppingapi.dto.auth.LoginRequest();
        loginRequest.setUsername("adminuser");
        loginRequest.setPassword("adminpass123");
        var loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();

        // Create product with admin token
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setPrice(new BigDecimal("99.99"));
        request.setStockQuantity(10);
        request.setCategory("Electronics");

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.active").value(true));
    }

    /**
     * Tests that createProduct with negative price returns bad request.
     */
    @Test
    void createProduct_WithNegativePrice_ShouldReturnBadRequest() throws Exception {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Test Product");
        request.setPrice(new BigDecimal("-10.00"));
        request.setStockQuantity(10);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").exists());
    }

    /**
     * Tests that createProduct with empty name returns bad request.
     */
    @Test
    void createProduct_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("");
        request.setPrice(new BigDecimal("99.99"));
        request.setStockQuantity(10);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    /**
     * Tests that getAllProducts returns paged results.
     */
    @Test
    void getAllProducts_ShouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists());
    }
}