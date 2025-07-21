package org.utku.shoppingapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.utku.shoppingapi.dto.request.UpdateUserRequest;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.repository.UserRepository;

import java.util.Optional;

/**
 * Implementation of UserService.
 * Handles all business logic and security for user management operations.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    /**
     * Repository for user data access.
     */
    private final UserRepository userRepository;
    /**
     * Password encoder for secure password storage.
     */
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all users in the system with pagination support.
     * @param pageable Pagination parameters
     * @return Page of users
     */
    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Finds a user by their unique identifier.
     * @param id User ID
     * @return Optional containing the user if found
     */
    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Creates a new user account.
     * @param user User entity to create
     * @return Created user entity
     */
    @Override
    public User createUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new org.utku.shoppingapi.exception.ValidationException("Username cannot be empty");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new org.utku.shoppingapi.exception.ValidationException("Invalid email format");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new org.utku.shoppingapi.exception.ValidationException("Password must be at least 6 characters");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Updates an existing user's information.
     * @param id User ID
     * @param request Update request containing new information
     * @return Updated user entity
     */
    @Override
    public User updateUser(Long id, UpdateUserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        if (request.getUsername() != null) {
            existing.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            existing.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) {
            existing.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existing.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            existing.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEnabled() != null) {
            existing.setEnabled(request.getEnabled());
        }
        if (StringUtils.hasText(request.getPassword())) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return userRepository.save(existing);
    }

    /**
     * Deletes a user account. Admin users cannot be deleted.
     * @param id User ID
     */
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        if (user.getRoles().stream().anyMatch(role -> role != null && role.name().equalsIgnoreCase("ADMIN"))) {
            throw new IllegalArgumentException("Admin users can't be deleted.");
        }
        user.setUsername("deleted_user_" + id);
        user.setEmail("deleted_" + id + "@deleted.com");
        user.setFirstName("Deleted");
        user.setLastName("User");
        user.setPhoneNumber(null);
        user.setEnabled(false);
        user.setPassword(null);

        userRepository.save(user);
    }
}