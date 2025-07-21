package org.utku.shoppingapi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Login request DTO for user authentication.
 */
@Data
public class LoginRequest {
    /**
     * Username for login authentication.
     */
    @NotBlank(message = "Username cannot be empty")
    private String username;
    /**
     * Password for login authentication.
     */
    @NotBlank(message = "Password cannot be empty")
    private String password;
}