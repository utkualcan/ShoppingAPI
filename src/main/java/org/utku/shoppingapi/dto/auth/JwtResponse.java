package org.utku.shoppingapi.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT response DTO containing token information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    /**
     * JWT token string for authentication.
     */
    private String token;
    /**
     * Token type (usually "Bearer").
     */
    private String type = "Bearer";
    /**
     * Unique identifier of the authenticated user.
     */
    private Long id;
    /**
     * Username of the authenticated user.
     */
    private String username;
    /**
     * Email address of the authenticated user.
     */
    private String email;
    /**
     * Set of roles assigned to the user (e.g., USER, ADMIN).
     */
    private java.util.Set<String> roles;
    /**
     * Token expiration time in seconds.
     */
    private Long expiresIn;

    public JwtResponse(String token, Long id, String username, String email, java.util.Set<String> roles, Long expiresIn) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.expiresIn = expiresIn;
    }
}