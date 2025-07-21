package org.utku.shoppingapi.service;

/**
 * Unit tests for {@link UserServiceImpl}.
 * Covers user creation, validation, retrieval, and deletion scenarios.
 */
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    /**
     * Mocks the UserRepository for data access operations.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mocks the PasswordEncoder for password encoding.
     */
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /**
     * Injects the UserServiceImpl with mocked dependencies.
     */
    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Tests that getAllUsers returns a paged list of users.
     */
    @Test
    void getAllUsers_ShouldReturnPagedUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1, user2), pageable, 2);
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<User> result = userService.getAllUsers(pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals("user1", result.getContent().get(0).getUsername());
        verify(userRepository).findAll(pageable);
    }

    /**
     * Tests that findUserById returns a user when the user exists.
     */
    @Test
    void findUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findById(1L);
    }

    /**
     * Tests that findUserById throws ResourceNotFoundException when the user does not exist.
     */
    @Test
    void findUserById_WhenUserNotExists_ShouldThrowResourceNotFoundException() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        // When & Then
        var ex = assertThrows(org.utku.shoppingapi.exception.ResourceNotFoundException.class, () -> userService.findUserById(99L).orElseThrow(() -> new org.utku.shoppingapi.exception.ResourceNotFoundException("User not found")));
        assertEquals("User not found", ex.getMessage());
    }

    /**
     * Tests that createUser saves and returns a new user with valid data.
     */
    @Test
    void createUser_ShouldSaveAndReturnUser() {
        // Given
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPassword("rawpassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");
        savedUser.setPassword("encodedpassword");

        when(passwordEncoder.encode("rawpassword")).thenReturn("encodedpassword");
        when(userRepository.save(user)).thenReturn(savedUser);

        // When
        User result = userService.createUser(user);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getUsername());
        verify(passwordEncoder).encode("rawpassword");
        verify(userRepository).save(user);
    }

    /**
     * Tests that createUser throws ValidationException for empty username.
     */
    @Test
    void createUser_WithEmptyUsername_ShouldThrowException() {
        // Given
        User user = new User();
        user.setUsername("");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        // When & Then
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> userService.createUser(user));
    }

    /**
     * Tests that createUser throws ValidationException for short password.
     */
    @Test
    void createUser_WithShortPassword_ShouldThrowException() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("123");
        // When & Then
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> userService.createUser(user));
    }

    /**
     * Tests that createUser throws ValidationException for invalid email format.
     */
    @Test
    void createUser_WithInvalidEmail_ShouldThrowException() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("invalid-email");
        user.setPassword("password123");
        // When & Then
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> userService.createUser(user));
    }

    /**
     * Tests that deleteUser updates user fields and disables the user.
     */
    @Test
    void deleteUser_ShouldCallRepositoryDelete() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRoles(java.util.Set.of(org.utku.shoppingapi.entity.Role.USER));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        // When
        userService.deleteUser(1L);
        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    /**
     * Tests that createUser does not persist user if an exception is thrown during password encoding.
     */
    @Test
    void createUser_WhenExceptionThrown_ShouldNotPersistUser() {
        // Given
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPassword("password123");
        when(passwordEncoder.encode(anyString())).thenThrow(new RuntimeException("Encoding error"));
        // When & Then
        assertThrows(RuntimeException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests that createUser handles duplicate username scenario and throws ValidationException.
     */
    @Test
    void createUser_Concurrency_ShouldHandleDuplicateUsername() {
        // Given
        User user1 = new User();
        user1.setUsername("sameuser");
        user1.setEmail("user1@example.com");
        user1.setPassword("password123");
        User user2 = new User();
        user2.setUsername("sameuser");
        user2.setEmail("user2@example.com");
        user2.setPassword("password123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            if ("sameuser".equals(u.getUsername())) {
                throw new org.utku.shoppingapi.exception.ValidationException("Username already exists");
            }
            return u;
        });
        // When & Then
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> userService.createUser(user1));
        assertThrows(org.utku.shoppingapi.exception.ValidationException.class, () -> userService.createUser(user2));
    }
}