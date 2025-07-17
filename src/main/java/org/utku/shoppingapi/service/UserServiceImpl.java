package org.utku.shoppingapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.dto.request.UpdateUserRequest;
import org.utku.shoppingapi.entity.Role;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, EntityMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllActiveUsers(pageable);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id).filter(User::getEnabled);
    }

    @Override
    public User createUser(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Role> defaultRoles = new HashSet<>();
            defaultRoles.add(Role.USER);
            user.setRoles(defaultRoles);
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UpdateUserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + id));

        mapper.updateEntityFromRequest(existing, request);

        if (StringUtils.hasText(request.getPassword())) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + id));

        if (user.getRoles().stream().anyMatch(role -> role != null && role.name().equalsIgnoreCase("ADMIN"))) {
            throw new IllegalArgumentException("Admin users can't be deleted.");
        }

        user.setUsername("deleted_user_" + id);
        user.setEmail("deleted_" + id + "@deleted.com");
        user.setFirstName("Deleted");
        user.setLastName("User");
        user.setPhoneNumber(null);
        user.setEnabled(false);
        user.setPassword(null);

        userRepository.save(user);
    }
}