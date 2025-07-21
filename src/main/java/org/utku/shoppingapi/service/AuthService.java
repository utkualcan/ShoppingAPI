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
import org.utku.shoppingapi.dto.auth.LogoutResponse;
import org.utku.shoppingapi.dto.auth.MessageResponse;
import org.utku.shoppingapi.dto.auth.RegisterRequest;
import org.utku.shoppingapi.dto.auth.UserInfoResponse;
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

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

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
        // Authenticate user credentials and generate JWT
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
        // Retrieve current authenticated user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("User not authenticated");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Get current user information as DTO.
     * 
     * @return UserInfoResponse with current user details
     */
    public UserInfoResponse getCurrentUserInfo() {
        // Map current user entity to UserInfoResponse DTO
        User user = getCurrentUser();
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.name())
                .collect(Collectors.toSet());
        
        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getPhoneNumber(),
                roles,
                user.getCreatedAt(),
                user.getEnabled()
        );
    }

    /**
     * Logout user by blacklisting their JWT token.
     * 
     * @param token JWT token to blacklist
     * @return LogoutResponse with success message
     */
    public LogoutResponse logoutUser(String token) {
        // Extract token from Authorization header if it includes "Bearer "
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        
        // Add token to blacklist
        tokenBlacklistService.blacklistToken(jwtToken);
        
        // Clear security context
        SecurityContextHolder.clearContext();
        
        return new LogoutResponse("User logged out successfully");
    }
}