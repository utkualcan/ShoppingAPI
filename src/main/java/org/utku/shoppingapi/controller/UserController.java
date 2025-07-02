package org.utku.shoppingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.dto.UserDto;
import org.utku.shoppingapi.dto.request.CreateUserRequest;
import org.utku.shoppingapi.dto.request.UpdateUserRequest;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.service.UserService;
import org.utku.shoppingapi.mapper.EntityMapper;

/**
 * REST Controller for managing user operations.
 * This controller handles all HTTP requests related to user management including:
 * - Creating new users
 * - Retrieving user information
 * - Updating existing users
 * - Deleting users
 * 
 * All endpoints are prefixed with '/api/users' and return JSON responses.
 * The controller uses DTOs for data transfer and includes proper validation.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "1. User Management", description = "API for user management operations")
public class UserController {

    private final UserService userService;
    private final EntityMapper mapper;

    /**
     * Constructor for dependency injection.
     * 
     * @param userService Service layer for user business logic
     * @param mapper Entity to DTO mapper for data transformation
     */
    public UserController(UserService userService, EntityMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    /**
     * Retrieves all users with pagination support.
     * Returns a paginated list of users sorted by ID in ascending order by default.
     * 
     * @param pageable Pagination parameters (page, size, sort)
     * @return Page of UserDto objects containing user information
     */
    @GetMapping
    @Operation(summary = "1. List all users", description = "Retrieve paginated list of all users")
    public Page<UserDto> getAllUsers(
            @Parameter(hidden = true) @PageableDefault(
                size = AppConstants.DEFAULT_PAGE_SIZE, 
                sort = AppConstants.DEFAULT_SORT_FIELD
            ) Pageable pageable) {
        return userService.getAllUsers(pageable).map(mapper::toDto);
    }

    /**
     * Retrieves a specific user by their ID.
     * 
     * @param id The unique identifier of the user
     * @return ResponseEntity containing UserDto if found, or 404 Not Found if user doesn't exist
     */
    @GetMapping("/{id}")
    @Operation(summary = "3. Get user by ID", description = "Retrieve a specific user by their unique identifier")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new user account.
     * Validates the request data and creates a new user with the provided information.
     * 
     * @param request CreateUserRequest containing user registration data
     * @return UserDto representing the newly created user
     */
    @PostMapping
    @Operation(summary = "2. Create new user", description = "Create a new user account with the provided information")
    public UserDto createUser(@Valid @RequestBody CreateUserRequest request) {
        // Convert request to entity using mapper
        User user = mapper.toEntity(request);
        
        // Save user and return DTO representation
        return mapper.toDto(userService.createUser(user));
    }

    /**
     * Updates an existing user's information.
     * Only updates fields that are provided in the request (partial update).
     * 
     * @param id The unique identifier of the user to update
     * @param request UpdateUserRequest containing updated information
     * @return ResponseEntity containing the updated UserDto
     */
    @PutMapping("/{id}")
    @Operation(summary = "4. Update user", description = "Update an existing user's information")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        // Update user using service with request data
        User updatedUser = userService.updateUser(id, request);
        
        return ResponseEntity.ok(mapper.toDto(updatedUser));
    }

    /**
     * Deletes a user account.
     * Permanently removes the user from the system.
     * 
     * @param id The unique identifier of the user to delete
     * @return ResponseEntity with 204 No Content status indicating successful deletion
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "5. Delete user", description = "Permanently delete a user account")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}