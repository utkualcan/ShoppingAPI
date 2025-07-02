package org.utku.shoppingapi.service;

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

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

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

    @Test
    void findUserById_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findUserById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        // Given
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");
        
        when(userRepository.save(user)).thenReturn(savedUser);

        // When
        User result = userService.createUser(user);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_ShouldCallRepositoryDelete() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        
        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }
}