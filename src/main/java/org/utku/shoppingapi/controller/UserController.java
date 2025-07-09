package org.utku.shoppingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.dto.UserDto;
import org.utku.shoppingapi.dto.request.CreateUserRequest;
import org.utku.shoppingapi.dto.request.UpdateUserRequest;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.service.UserService;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/users")
@Tag(name = "1. User Management", description = "API for user management operations")
public class UserController {

    private final UserService userService;
    private final EntityMapper mapper;

    public UserController(UserService userService, EntityMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "1. List all users", description = "Retrieve paginated list of all users")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserDto> getAllUsers(
            @Parameter(hidden = true) @PageableDefault(
                    size = AppConstants.DEFAULT_PAGE_SIZE,
                    sort = AppConstants.DEFAULT_SORT_FIELD
            ) Pageable pageable) {
        return userService.getAllUsers(pageable).map(mapper::toDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "3. Get user by ID", description = "Retrieve a specific user by their unique identifier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "2. Create new user", description = "Create a new user account with the provided information")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = mapper.toEntity(request);
        return mapper.toDto(userService.createUser(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "4. Update user", description = "Update an existing user's information")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(mapper.toDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "5. Delete user", description = "Permanently delete a user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + id));

        if (user.getRoles().stream().anyMatch(role -> role != null && role.toString().equalsIgnoreCase("ADMIN"))) {
            throw new IllegalArgumentException("Admin kullanıcılar silinemez.");
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}