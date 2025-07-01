package org.utku.shoppingapi.service;

import org.springframework.stereotype.Service;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (user.getUsername() != null) existing.setUsername(user.getUsername());
        if (user.getEmail() != null) existing.setEmail(user.getEmail());
        if (user.getPassword() != null) existing.setPassword(user.getPassword());
        if (user.getFirstName() != null) existing.setFirstName(user.getFirstName());
        if (user.getLastName() != null) existing.setLastName(user.getLastName());
        if (user.getPhoneNumber() != null) existing.setPhoneNumber(user.getPhoneNumber());
        if (user.getEnabled() != null) existing.setEnabled(user.getEnabled());
        if (user.getRoles() != null && !user.getRoles().isEmpty()) existing.setRoles(user.getRoles());

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User addFavorite(Long userId, Product product) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        user.addFavorite(product);
        return userRepository.save(user);
    }

    @Override
    public User removeFavorite(Long userId, Product product) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        user.removeFavorite(product);
        return userRepository.save(user);
    }

    @Override
    public List<Product> getFavoriteProducts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return user.getFavoriteProducts();
    }
}