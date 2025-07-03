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
 * REST Controller for managing user operations in the Shopping API.
 * 
 * <p>This controller provides comprehensive user management functionality including:
 * <ul>
 *   <li>User registration and account creation</li>
 *   <li>User profile retrieval with pagination support</li>
 *   <li>User information updates (partial updates supported)</li>
 *   <li>User account deletion with GDPR-compliant anonymization</li>
 * </ul>
 * 
 * <p>All endpoints follow RESTful conventions and are prefixed with '/api/users'.
 * The controller uses Data Transfer Objects (DTOs) for request/response handling
 * and includes comprehensive input validation using Bean Validation annotations.
 * 
 * <p>Security considerations:
 * <ul>
 *   <li>All input data is validated before processing</li>
 *   <li>User deletion follows GDPR compliance with data anonymization</li>
 *   <li>Sensitive information is filtered in response DTOs</li>
 * </ul>
 * 
 * @author Shopping API Development Team
 * @version 1.0
 * @since 1.0
 * @see UserService
 * @see UserDto
 * @see CreateUserRequest
 * @see UpdateUserRequest
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "1. User Management", description = "API for user management operations")
public class UserController {

    private final UserService userService;
    private final EntityMapper mapper;

    /**
     * Constructs a new UserController with required dependencies.
     * 
     * <p>This constructor is used by Spring's dependency injection container
     * to inject the required service and mapper dependencies.
     * 
     * @param userService The service layer component that handles user business logic
     *                   and data persistence operations
     * @param mapper The mapper component responsible for converting between
     *              entity objects and DTOs
     * @throws IllegalArgumentException if any parameter is null
     */
    public UserController(UserService userService, EntityMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    /**
     * Retrieves all users with pagination and sorting support.
     * 
     * <p>This endpoint returns a paginated list of all users in the system.
     * By default, results are sorted by user ID in ascending order and
     * limited to the default page size defined in application constants.
     * 
     * <p>Pagination parameters can be customized using query parameters:
     * <ul>
     *   <li>page: Zero-based page index (default: 0)</li>
     *   <li>size: Number of records per page (default: 20)</li>
     *   <li>sort: Sort criteria in format 'property,direction' (default: 'id,asc')</li>
     * </ul>
     * 
     * <p>Example usage:
     * <pre>
     * GET /api/users?page=0&size=10&sort=username,asc
     * </pre>
     * 
     * @param pageable Pagination and sorting parameters automatically bound from request
     * @return Page containing UserDto objects with pagination metadata
     * @see UserDto
     * @see AppConstants#DEFAULT_PAGE_SIZE
     * @see AppConstants#DEFAULT_SORT_FIELD
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
     * Retrieves a specific user by their unique identifier.
     * 
     * <p>This endpoint fetches detailed information about a single user
     * identified by their unique ID. The response includes all non-sensitive
     * user information formatted as a UserDto.
     * 
     * <p>Response scenarios:
     * <ul>
     *   <li>200 OK: User found and returned successfully</li>
     *   <li>404 Not Found: No user exists with the specified ID</li>
     * </ul>
     * 
     * <p>Example usage:
     * <pre>
     * GET /api/users/123
     * </pre>
     * 
     * @param id The unique identifier of the user to retrieve.
     *          Must be a positive long value.
     * @return ResponseEntity containing UserDto if user exists,
     *         or 404 Not Found response if user doesn't exist
     * @see UserDto
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
     * Creates a new user account in the system.
     * 
     * <p>This endpoint handles user registration by creating a new user account
     * with the provided information. All input data is validated according to
     * the constraints defined in the CreateUserRequest class.
     * 
     * <p>Validation includes:
     * <ul>
     *   <li>Username uniqueness and format validation</li>
     *   <li>Email format validation and uniqueness check</li>
     *   <li>Password strength requirements</li>
     *   <li>Required field presence validation</li>
     * </ul>
     * 
     * <p>Upon successful creation, the user account is immediately active
     * and ready for use. Sensitive information like passwords are not
     * included in the response.
     * 
     * <p>Example request body:
     * <pre>
     * {
     *   "username": "john_doe",
     *   "email": "john@example.com",
     *   "password": "securePassword123",
     *   "firstName": "John",
     *   "lastName": "Doe"
     * }
     * </pre>
     * 
     * @param request CreateUserRequest containing all required user registration data.
     *               Must pass validation constraints.
     * @return UserDto representing the newly created user with generated ID
     *         and timestamps, excluding sensitive information
     * @throws ValidationException if request data fails validation
     * @throws DataIntegrityViolationException if username or email already exists
     * @see CreateUserRequest
     * @see UserDto
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
     * Updates an existing user's information with partial update support.
     * 
     * <p>This endpoint allows modification of user account information using
     * a partial update approach. Only fields provided in the request body
     * will be updated, while null or missing fields will remain unchanged.
     * 
     * <p>Updatable fields include:
     * <ul>
     *   <li>firstName - User's first name</li>
     *   <li>lastName - User's last name</li>
     *   <li>phoneNumber - Contact phone number</li>
     *   <li>enabled - Account status (admin only)</li>
     * </ul>
     * 
     * <p>Note: Critical fields like username, email, and password cannot be
     * updated through this endpoint for security reasons. Separate endpoints
     * should be used for those operations.
     * 
     * <p>Example request body (partial update):
     * <pre>
     * {
     *   "firstName": "Jane",
     *   "phoneNumber": "+1-555-0123"
     * }
     * </pre>
     * 
     * @param id The unique identifier of the user to update.
     *          Must correspond to an existing user.
     * @param request UpdateUserRequest containing the fields to update.
     *               Only non-null fields will be applied.
     * @return ResponseEntity containing the updated UserDto with all current information
     * @throws ResourceNotFoundException if no user exists with the specified ID
     * @throws ValidationException if request data fails validation
     * @see UpdateUserRequest
     * @see UserDto
     */
    @PutMapping("/{id}")
    @Operation(summary = "4. Update user", description = "Update an existing user's information")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        // Update user using service with request data
        User updatedUser = userService.updateUser(id, request);
        
        return ResponseEntity.ok(mapper.toDto(updatedUser));
    }

    /**
     * Deletes a user account with GDPR-compliant data anonymization.
     * 
     * <p>This endpoint handles user account deletion in compliance with data
     * protection regulations (GDPR). Instead of permanently removing the user
     * record, the system performs data anonymization to preserve referential
     * integrity while protecting user privacy.
     * 
     * <p>Anonymization process includes:
     * <ul>
     *   <li>Username changed to "deleted_user_[ID]"</li>
     *   <li>Email changed to "deleted_[ID]@deleted.com"</li>
     *   <li>Personal information (names, phone) cleared or anonymized</li>
     *   <li>Account disabled to prevent login</li>
     *   <li>Order history preserved for business records</li>
     * </ul>
     * 
     * <p>This approach ensures:
     * <ul>
     *   <li>GDPR compliance for "right to be forgotten"</li>
     *   <li>Preservation of business transaction history</li>
     *   <li>Maintenance of database referential integrity</li>
     *   <li>Prevention of cascading deletions</li>
     * </ul>
     * 
     * <p>Response status:
     * <ul>
     *   <li>204 No Content: User successfully anonymized</li>
     *   <li>404 Not Found: No user exists with the specified ID</li>
     * </ul>
     * 
     * @param id The unique identifier of the user account to delete/anonymize.
     *          Must correspond to an existing user.
     * @return ResponseEntity with 204 No Content status indicating successful
     *         completion of the anonymization process
     * @throws ResourceNotFoundException if no user exists with the specified ID
     * @see UserService#deleteUser(Long)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "5. Delete user", description = "Permanently delete a user account")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}