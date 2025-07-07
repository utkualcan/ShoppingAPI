package org.utku.shoppingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.dto.auth.JwtResponse;
import org.utku.shoppingapi.dto.auth.LoginRequest;
import org.utku.shoppingapi.dto.auth.MessageResponse;
import org.utku.shoppingapi.dto.auth.RegisterRequest;
import org.utku.shoppingapi.service.AuthService;

/**
 * REST controller for authentication operations.
 * Handles user registration, login, and token management.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new user account.
     * 
     * @param registerRequest Registration request data
     * @return MessageResponse with success message and user details
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account with USER role")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        MessageResponse response = authService.registerUser(registerRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticate user and generate JWT token.
     * 
     * @param loginRequest Login request data
     * @return JwtResponse with token and user details
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and generate JWT token")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }
}