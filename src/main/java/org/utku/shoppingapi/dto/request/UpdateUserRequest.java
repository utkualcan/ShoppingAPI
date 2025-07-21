package org.utku.shoppingapi.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.utku.shoppingapi.constants.AppConstants;

/**
 * Data Transfer Object for updating existing users.
 * This class represents the request payload when updating user information.
 * All fields are optional to support partial updates.
 */
@Data
public class UpdateUserRequest {
    
    /**
     * Optional username update.
     * Must be within allowed length range if provided.
     */
    @Size(min = AppConstants.MIN_USERNAME_LENGTH, max = AppConstants.MAX_USERNAME_LENGTH, 
          message = "Username must be between " + AppConstants.MIN_USERNAME_LENGTH + 
                   "-" + AppConstants.MAX_USERNAME_LENGTH + " characters")
    private String username;
    /**
     * Optional email address update.
     * Must be a valid email format if provided.
     */
    @Email(message = "Please enter a valid email address")
    @Size(max = AppConstants.MAX_EMAIL_LENGTH, 
          message = "Email cannot exceed " + AppConstants.MAX_EMAIL_LENGTH + " characters")
    private String email;
    /**
     * Optional password update.
     * Must meet minimum length requirements if provided.
     */
    @Size(min = AppConstants.MIN_PASSWORD_LENGTH, 
          message = "Password must be at least " + AppConstants.MIN_PASSWORD_LENGTH + " characters")
    private String password;
    /**
     * Optional first name update.
     */
    @Size(max = AppConstants.MAX_NAME_LENGTH, 
          message = "First name cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters")
    private String firstName;
    /**
     * Optional last name update.
     */
    @Size(max = AppConstants.MAX_NAME_LENGTH, 
          message = "Last name cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters")
    private String lastName;
    /**
     * Optional phone number update.
     * Must be within maximum allowed length to match database constraint.
     */
    @Size(max = AppConstants.MAX_PHONE_LENGTH, 
          message = "Phone number cannot exceed " + AppConstants.MAX_PHONE_LENGTH + " characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Please enter a valid phone number")
    private String phoneNumber;
    /**
     * Optional account status update.
     */
    private Boolean enabled;
}