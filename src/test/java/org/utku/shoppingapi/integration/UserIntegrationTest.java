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
import org.utku.shoppingapi.dto.request.CreateUserRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for user endpoints.
 * Validates user creation, email and password validation, and user retrieval scenarios.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

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
     * Injects the PasswordEncoder for password encoding operations.
     */
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /**
     * Stores the admin JWT token for authenticated requests in tests.
     */
    private String adminToken;

    /**
     * Sets up an admin user and retrieves a JWT token before each test.
     */
    @org.junit.jupiter.api.BeforeEach
    void setupAdmin() throws Exception {
        // Admin kullanıcıyı ekle
        var adminUser = new org.utku.shoppingapi.entity.User();
        adminUser.setUsername("adminuser");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("adminpass123"));
        adminUser.setEnabled(true);
        adminUser.addRole(org.utku.shoppingapi.entity.Role.ADMIN);
        userRepository.save(adminUser);

        // Login ve token al
        var loginRequest = new org.utku.shoppingapi.dto.auth.LoginRequest();
        loginRequest.setUsername("adminuser");
        loginRequest.setPassword("adminpass123");
        var loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        adminToken = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();
    }

    /**
     * Tests that createUser with valid data returns the created user.
     */
    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("Test");
        request.setLastName("User");

        mockMvc.perform(post("/api/users")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"));
    }

    /**
     * Tests that createUser with invalid email returns bad request.
     */
    @Test
    void createUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setEmail("invalid-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    /**
     * Tests that createUser with short password returns bad request.
     */
    @Test
    void createUser_WithShortPassword_ShouldReturnBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("123");

        mockMvc.perform(post("/api/users")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").exists());
    }

    /**
     * Tests that getAllUsers returns paged results.
     */
    @Test
    void getAllUsers_ShouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists());
    }
}