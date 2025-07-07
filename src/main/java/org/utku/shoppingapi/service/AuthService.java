package org.utku.shoppingapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.dto.auth.JwtResponse;
import org.utku.shoppingapi.dto.auth.LoginRequest;
import org.utku.shoppingapi.dto.auth.MessageResponse;
import org.utku.shoppingapi.dto.auth.RegisterRequest;
import org.utku.shoppingapi.entity.Role;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.repository.UserRepository;
import org.utku.shoppingapi.security.JwtUtil;
import org.utku.shoppingapi.security.UserPrincipal;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for handling authentication operations.
 * Provides user registration, login, and token management functionality.
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user with USER role by default.
     * 
     * @param registerRequest Registration request data
     * @return MessageResponse with success message and user details
     * @throws RuntimeException if username or email already exists
     */
    public MessageResponse registerUser(RegisterRequest registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setEnabled(true);

        // Assign USER role by default
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);

        userRepository.save(user);

        return new MessageResponse("User registered successfully!", user.getId(), "USER");
    }

    /**
     * Authenticate user and generate JWT token.
     * 
     * @param loginRequest Login request data
     * @return JwtResponse with token and user details
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Set<String> roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        return new JwtResponse(jwt, userPrincipal.getId(), userPrincipal.getUsername(), 
                userPrincipal.getEmail(), roles, 86400L);
    }

    /**
     * Get current authenticated user.
     * 
     * @return Current user
     * @throws ResourceNotFoundException if user not found
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("User not authenticated");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}