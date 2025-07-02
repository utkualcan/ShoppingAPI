package org.utku.shoppingapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.dto.request.UpdateUserRequest;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.repository.UserRepository;

import java.util.Optional;

/**
 * Implementation of UserService interface.
 * This service class handles all business logic related to user management including:
 * - CRUD operations for users
 * - User validation and data processing
 * - Integration with UserRepository for data persistence
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityMapper mapper;

    /**
     * Constructor for dependency injection.
     * 
     * @param userRepository Repository for user data access
     * @param mapper Entity mapper for data transformations
     */
    public UserServiceImpl(UserRepository userRepository, EntityMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves all users with pagination support.
     * 
     * @param pageable Pagination parameters
     * @return Page of users
     */
    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Finds a user by their unique identifier.
     * 
     * @param id The user ID
     * @return Optional containing the user if found
     */
    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Creates a new user account.
     * 
     * @param user The user entity to create
     * @return The created user entity
     */
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates an existing user's information using request data.
     * Only updates fields that are not null in the request to support partial updates.
     * 
     * @param id The ID of the user to update
     * @param request Update request containing new data
     * @return The updated user entity
     * @throws ResourceNotFoundException if user is not found
     */
    @Override
    public User updateUser(Long id, UpdateUserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + id));

        // Use mapper to update entity from request
        mapper.updateEntityFromRequest(existing, request);

        return userRepository.save(existing);
    }

    /**
     * Deletes a user account by ID.
     * Validates that the user exists before deletion.
     * 
     * @param id The ID of the user to delete
     * @throws ResourceNotFoundException if user is not found
     */
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + id);
        }
        userRepository.deleteById(id);
    }
}