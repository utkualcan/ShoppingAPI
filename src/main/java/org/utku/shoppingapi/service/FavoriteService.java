package org.utku.shoppingapi.service;

import org.utku.shoppingapi.entity.Favorite;

import java.util.List;

public interface FavoriteService {
    Favorite addFavorite(Long userId, Long productId);
    void removeFavorite(Long userId, Long productId);
    List<Favorite> getFavoritesByUserId(Long userId);
    boolean isFavorite(Long userId, Long productId);
}