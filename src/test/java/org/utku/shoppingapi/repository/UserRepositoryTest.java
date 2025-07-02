package org.utku.shoppingapi.repository;

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

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    void findByUsername_WhenUserNotExists_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(found.isPresent());
    }

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