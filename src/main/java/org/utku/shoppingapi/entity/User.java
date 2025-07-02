package org.utku.shoppingapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.utku.shoppingapi.constants.AppConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity representing a user in the shopping system.
 * This class contains all user-related information including:
 * - Authentication details (username, email, password)
 * - Personal information (first name, last name, phone)
 * - Account status and roles
 * - Relationships with cart and favorites
 * 
 * The entity enforces unique constraints on username and email fields.
 * Uses JPA annotations for database mapping and validation.
 * 
 * @author Shopping API Team
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Unique identifier for the user.
     * Auto-generated using database identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username for the user account.
     * Must be within allowed length range and unique across the system.
     */
    @Column(nullable = false, unique = true, length = AppConstants.MAX_USERNAME_LENGTH)
    @NotBlank(message = "Username cannot be empty")
    @Size(min = AppConstants.MIN_USERNAME_LENGTH, max = AppConstants.MAX_USERNAME_LENGTH, 
          message = "Username must be between " + AppConstants.MIN_USERNAME_LENGTH + 
                   "-" + AppConstants.MAX_USERNAME_LENGTH + " characters")
    private String username;

    /**
     * Email address for the user account.
     * Must be a valid email format and unique across the system.
     */
    @Column(nullable = false, unique = true, length = AppConstants.MAX_EMAIL_LENGTH)
    @NotBlank(message = "Email address cannot be empty")
    @Email(message = "Please enter a valid email address")
    private String email;

    /**
     * Encrypted password for the user account.
     * Must meet minimum length requirements for basic security.
     */
    @Column(nullable = false)
    @NotBlank(message = "Password cannot be empty")
    @Size(min = AppConstants.MIN_PASSWORD_LENGTH, 
          message = "Password must be at least " + AppConstants.MIN_PASSWORD_LENGTH + " characters")
    private String password;

    /**
     * Optional first name of the user.
     */
    @Column(length = AppConstants.MAX_NAME_LENGTH)
    @Size(max = AppConstants.MAX_NAME_LENGTH, 
          message = "First name cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters")
    private String firstName;

    /**
     * Optional last name of the user.
     */
    @Column(length = AppConstants.MAX_NAME_LENGTH)
    @Size(max = AppConstants.MAX_NAME_LENGTH, 
          message = "Last name cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters")
    private String lastName;

    /**
     * Optional phone number for the user.
     */
    @Column(length = AppConstants.MAX_PHONE_LENGTH)
    private String phoneNumber;

    /**
     * Indicates whether the user account is enabled/active.
     * Disabled accounts cannot log in or perform operations.
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * Timestamp when the user account was created.
     * Automatically set on entity creation and never updated.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user account was last updated.
     * Automatically updated whenever the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Set of roles assigned to the user.
     * Roles determine what operations the user can perform.
     * Stored as strings in a separate user_roles table.
     */
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    /**
     * Shopping cart associated with this user.
     * One-to-one relationship, lazily loaded.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;

    /**
     * List of favorite products for this user.
     * One-to-many relationship with cascade operations and orphan removal.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    /**
     * Returns the full name of the user.
     * Combines first and last name, or returns individual names if only one is available.
     * Falls back to username if no names are provided.
     * 
     * @return Full name string or username as fallback
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }

    /**
     * Checks if the user has a specific role.
     * 
     * @param role The role to check for
     * @return true if the user has the specified role, false otherwise
     */
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    /**
     * Adds a role to the user's role set.
     * 
     * @param role The role to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Removes a role from the user's role set.
     * 
     * @param role The role to remove
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

}