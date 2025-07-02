package org.utku.shoppingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.constants.AppConstants;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity data access operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 * 
 * Provides methods for:
 * - Finding users by username or email
 * - Querying active users
 * - Checking existence of usernames and emails
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their username.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Finds a user by their email address.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Retrieves all active users from the database.
     * 
     * @return List of active users
     */
    @Query(AppConstants.Queries.FIND_ACTIVE_USERS)
    List<User> findAllActiveUsers();
    
    /**
     * Finds an active user by their email address.
     * 
     * @param email the email address to search for
     * @return Optional containing the active user if found, empty otherwise
     */
    @Query(AppConstants.Queries.FIND_ACTIVE_USER_BY_EMAIL)
    Optional<User> findActiveUserByEmail(@Param("email") String email);
    
    /**
     * Checks if a username already exists in the database.
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Checks if an email address already exists in the database.
     * 
     * @param email the email address to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
}