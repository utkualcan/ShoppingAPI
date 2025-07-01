package org.utku.shoppingapi.service;

import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.entity.Product;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);

    User addFavorite(Long userId, Product product);
    User removeFavorite(Long userId, Product product);
    List<Product> getFavoriteProducts(Long userId);
}