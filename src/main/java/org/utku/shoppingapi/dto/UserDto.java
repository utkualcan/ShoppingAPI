package org.utku.shoppingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for user information.
 * This class represents user data sent to/from the client.
 * 
 * Contains:
 * - User identifier and account information
 * - Personal details and contact information
 * - Account status and role information
 * - Excludes sensitive data like passwords
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    /**
     * Unique identifier of the user.
     */
    private Long id;

    /**
     * Unique username for the account.
     */
    private String username;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * First name of the user.
     */
    private String firstName;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * Phone number of the user.
     */
    private String phoneNumber;

    /**
     * Indicates whether the user account is enabled.
     */
    private boolean enabled;

    /**
     * Set of roles assigned to the user (e.g., USER, ADMIN).
     */
    private Set<String> roles;

    /**
     * Timestamp when the user account was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user account was last updated.
     */
    private LocalDateTime updatedAt;
}