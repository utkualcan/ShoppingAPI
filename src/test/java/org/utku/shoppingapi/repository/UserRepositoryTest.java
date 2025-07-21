package org.utku.shoppingapi.repository;

/**
 * Unit tests for {@link UserRepository}.
 * Validates user persistence and retrieval operations.
 */

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.utku.shoppingapi.entity.Role;
import org.utku.shoppingapi.entity.User;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    /**
     * Provides an in-memory entity manager for test persistence operations.
     */
    @Autowired
    private TestEntityManager entityManager;

    /**
     * Injects the UserRepository for user data access operations.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Tests that findByUsername returns a user when the user exists.
     */
    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRoles(Set.of(Role.USER));
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("test@example.com", found.get().getEmail());
    }

    /**
     * Tests that findByUsername returns empty when the user does not exist.
     */
    @Test
    void findByUsername_WhenUserNotExists_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(found.isPresent());
    }

    /**
     * Tests that save persists a new user and returns the saved entity.
     */
    @Test
    void save_ShouldPersistUser() {
        // Given
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRoles(Set.of(Role.USER));

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());

        // Verify it's actually persisted
        User found = entityManager.find(User.class, saved.getId());
        assertNotNull(found);
        assertEquals("newuser", found.getUsername());
    }
}