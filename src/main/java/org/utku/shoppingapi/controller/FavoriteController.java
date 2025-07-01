package org.utku.shoppingapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utku.shoppingapi.entity.Favorite;
import org.utku.shoppingapi.service.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity<Favorite> addFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        return ResponseEntity.ok(favoriteService.addFavorite(userId, productId));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        favoriteService.removeFavorite(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public List<Favorite> getFavoritesByUserId(@PathVariable Long userId) {
        return favoriteService.getFavoritesByUserId(userId);
    }

    @GetMapping("/exists")
    public boolean isFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        return favoriteService.isFavorite(userId, productId);
    }
}