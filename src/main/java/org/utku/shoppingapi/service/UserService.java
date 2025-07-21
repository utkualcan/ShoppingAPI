package org.utku.shoppingapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.utku.shoppingapi.dto.request.UpdateUserRequest;
import org.utku.shoppingapi.entity.User;

import java.util.Optional;

/**
 * Service interface for user management operations.
 * Defines the contract for user-related business logic and security.
 */
public interface UserService {
    /**
     * Retrieves all users in the system with pagination support.
     * @param pageable Pagination parameters
     * @return Page of users
     */
    Page<User> getAllUsers(Pageable pageable);
    /**
     * Finds a user by their unique identifier.
     * @param id User ID
     * @return Optional containing the user if found
     */
    Optional<User> findUserById(Long id);
    /**
     * Creates a new user account.
     * @param user User entity to create
     * @return Created user entity
     */
    User createUser(User user);
    /**
     * Updates an existing user's information.
     * @param id User ID
     * @param request Update request containing new information
     * @return Updated user entity
     */
    User updateUser(Long id, UpdateUserRequest request);
    /**
     * Deletes a user account. Admin users cannot be deleted.
     * @param id User ID
     */
    void deleteUser(Long id);
}