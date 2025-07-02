package org.utku.shoppingapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.utku.shoppingapi.dto.request.UpdateUserRequest;
import org.utku.shoppingapi.entity.User;

import java.util.Optional;

/**
 * Service interface for user management operations.
 * Defines the contract for user-related business logic.
 */
public interface UserService {
    
    /**
     * Retrieves all users with pagination support.
     * 
     * @param pageable Pagination parameters
     * @return Page of users
     */
    Page<User> getAllUsers(Pageable pageable);
    
    /**
     * Finds a user by their unique identifier.
     * 
     * @param id The user ID
     * @return Optional containing the user if found
     */
    Optional<User> findUserById(Long id);
    
    /**
     * Creates a new user account.
     * 
     * @param user The user entity to create
     * @return The created user entity
     */
    User createUser(User user);
    
    /**
     * Updates an existing user's information using request data.
     * 
     * @param id The ID of the user to update
     * @param request Update request containing new data
     * @return The updated user entity
     */
    User updateUser(Long id, UpdateUserRequest request);
    
    /**
     * Deletes a user account by ID.
     * 
     * @param id The ID of the user to delete
     */
    void deleteUser(Long id);
}