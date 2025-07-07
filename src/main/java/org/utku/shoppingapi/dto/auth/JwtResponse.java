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
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private java.util.Set<String> roles;
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