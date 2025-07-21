package org.utku.shoppingapi.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for user information response.
 * Contains user details returned by the /auth/me endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    /**
     * Unique identifier of the user.
     */
    private Long id;
    /**
     * Username of the user.
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
     * Full name of the user (first + last name).
     */
    private String fullName;
    /**
     * Phone number of the user.
     */
    private String phoneNumber;
    /**
     * Set of roles assigned to the user (e.g., USER, ADMIN).
     */
    private Set<String> roles;
    /**
     * Timestamp when the user account was created.
     */
    private LocalDateTime createdAt;
    /**
     * Indicates whether the user account is enabled.
     */
    private Boolean enabled;
}