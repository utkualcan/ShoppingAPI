package org.utku.shoppingapi.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.validation.UniqueUsername;

/**
 * Register request DTO for user registration.
 */
@Data
public class RegisterRequest {
    
    @NotBlank(message = "Username cannot be empty")
    @Size(min = AppConstants.MIN_USERNAME_LENGTH, max = AppConstants.MAX_USERNAME_LENGTH, 
          message = "Username must be between " + AppConstants.MIN_USERNAME_LENGTH + 
                   "-" + AppConstants.MAX_USERNAME_LENGTH + " characters")
    @UniqueUsername
    private String username;
    
    @NotBlank(message = "Email address cannot be empty")
    @Email(message = "Please enter a valid email address")
    @Size(max = AppConstants.MAX_EMAIL_LENGTH, 
          message = "Email cannot exceed " + AppConstants.MAX_EMAIL_LENGTH + " characters")
    private String email;
    
    @NotBlank(message = "Password cannot be empty")
    @Size(min = AppConstants.MIN_PASSWORD_LENGTH, 
          message = "Password must be at least " + AppConstants.MIN_PASSWORD_LENGTH + " characters")
    private String password;
    
    @Size(max = AppConstants.MAX_NAME_LENGTH, 
          message = "First name cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters")
    private String firstName;
    
    @Size(max = AppConstants.MAX_NAME_LENGTH, 
          message = "Last name cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters")
    private String lastName;
    
    private String phoneNumber;
}