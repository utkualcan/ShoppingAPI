package org.utku.shoppingapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/favorites")
    public ResponseEntity<User> addFavorite(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(userService.addFavorite(id, product));
    }

    @DeleteMapping("/{id}/favorites")
    public ResponseEntity<User> removeFavorite(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(userService.removeFavorite(id, product));
    }

    @GetMapping("/{id}/favorites")
    public ResponseEntity<List<Product>> getFavorites(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getFavoriteProducts(id));
    }
}