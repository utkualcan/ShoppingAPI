package org.utku.shoppingapi.integration;

/**
 * Integration tests for public security endpoints.
 * Validates accessibility of registration endpoints without authentication.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.dto.auth.RegisterRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityPublicEndpointTest {

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
     * Tests that the public registration endpoint is accessible without authentication.
     */
    @Test
    void publicRegistrationEndpoint_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("publictestuser");
        request.setEmail("publictest@example.com");
        request.setPassword("password123");
        request.setFirstName("Public");
        request.setLastName("Test");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    /**
     * Tests that the specific registration endpoint is accessible without authentication.
     */
    @Test
    void specificRegistrationEndpoint_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("specifictestuser");
        request.setEmail("specifictest@example.com");
        request.setPassword("password123");
        request.setFirstName("Specific");
        request.setLastName("Test");

        // This test specifically validates that /api/auth/register works with our explicit permitAll() rule
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.role").value("USER"));
    }
}