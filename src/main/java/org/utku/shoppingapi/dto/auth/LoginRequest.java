package org.utku.shoppingapi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Login request DTO for user authentication.
 */
@Data
public class LoginRequest {
    
    @NotBlank(message = "Username cannot be empty")
    private String username;
    
    @NotBlank(message = "Password cannot be empty")
    private String password;
}