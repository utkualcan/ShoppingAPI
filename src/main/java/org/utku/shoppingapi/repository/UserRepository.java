package org.utku.shoppingapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.utku.shoppingapi.entity.User;

import java.util.Optional;

/**
 * Repository interface for User entity data access operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 *
 * Provides methods for:
 * - Finding users by username or email
 * - Checking existence of users
 * - Retrieving active users
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their username.
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all active users (enabled = true).
     * @param pageable pagination information
     * @return Page of active users
     */
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    Page<User> findAllActiveUsers(Pageable pageable);

    /**
     * Finds an active user by email address.
     * @param email the email to search for
     * @return Optional containing the active user if found
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.enabled = true")
    Optional<User> findActiveUserByEmail(String email);

    /**
     * Checks if a user exists by username.
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by email address.
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
}