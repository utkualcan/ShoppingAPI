package org.utku.shoppingapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.validation.UniqueUsername;

/**
 * Data Transfer Object for creating new user accounts.
 * This class represents the request payload when registering a new user.
 * 
 * Contains validation rules to ensure:
 * - Username is unique, not blank, and within length limits
 * - Email is valid format and not blank
 * - Password meets minimum security requirements
 * - Optional fields (names, phone) are within acceptable limits
 */
@Data
public class CreateUserRequest {
    
    /**
     * The unique username for the account.
     * Must be within allowed length range and unique across the system.
     */
    @NotBlank(message = "Username cannot be empty")
    @Size(min = AppConstants.MIN_USERNAME_LENGTH, max = AppConstants.MAX_USERNAME_LENGTH, 
          message = "Username must be between " + AppConstants.MIN_USERNAME_LENGTH + 
                   "-" + AppConstants.MAX_USERNAME_LENGTH + " characters")
    @UniqueUsername
    private String username;
    
    /**
     * The email address for the account.
     * Must be a valid email format and not blank.
     */
    @NotBlank(message = "Email address cannot be empty")
    @Email(message = "Please enter a valid email address")
    @Size(max = AppConstants.MAX_EMAIL_LENGTH, 
          message = "Email cannot exceed " + AppConstants.MAX_EMAIL_LENGTH + " characters")
    private String email;
    
    /**
     * The password for the account.
     * Must meet minimum length requirements for basic security.
     */
    @NotBlank(message = "Password cannot be empty")
    @Size(min = AppConstants.MIN_PASSWORD_LENGTH, 
          message = "Password must be at least " + AppConstants.MIN_PASSWORD_LENGTH + " characters")
    private String password;
    
    /**
     * Optional first name of the user.
     * Cannot exceed maximum allowed length if provided.
     */
    @Size(max = AppConstants.MAX_NAME_LENGTH, 
          message = "First name cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters")
    private String firstName;
    
    /**
     * Optional last name of the user.
     * Cannot exceed maximum allowed length if provided.
     */
    @Size(max = AppConstants.MAX_NAME_LENGTH, 
          message = "Last name cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters")
    private String lastName;
    
    /**
     * Optional phone number for the user.
     * Must match the pattern for valid phone numbers and be within length limit if provided.
     */
    @Size(max = AppConstants.MAX_PHONE_LENGTH, 
          message = "Phone number cannot exceed " + AppConstants.MAX_PHONE_LENGTH + " characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Please enter a valid phone number")
    private String phoneNumber;
}