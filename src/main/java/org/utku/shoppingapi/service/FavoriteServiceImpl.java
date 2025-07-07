package org.utku.shoppingapi.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utku.shoppingapi.constants.AppConstants;
import org.utku.shoppingapi.dto.FavoriteDto;
import org.utku.shoppingapi.dto.response.ApiResponse;
import org.utku.shoppingapi.entity.Favorite;
import org.utku.shoppingapi.entity.Product;
import org.utku.shoppingapi.entity.User;
import org.utku.shoppingapi.exception.ResourceNotFoundException;
import org.utku.shoppingapi.mapper.EntityMapper;
import org.utku.shoppingapi.repository.FavoriteRepository;
import org.utku.shoppingapi.repository.ProductRepository;
import org.utku.shoppingapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EntityMapper mapper;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, UserRepository userRepository, ProductRepository productRepository, EntityMapper mapper) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public FavoriteDto addFavorite(Long userId, Long productId) {
        User user = findUserById(userId);
        Product product = findProductById(productId);

        if (favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new IllegalArgumentException(AppConstants.ALREADY_IN_FAVORITES);
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        return mapper.toDto(favoriteRepository.save(favorite));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ApiResponse<String> removeFavorite(Long userId, Long productId) {
        // Corrected: Check for existence using the available findBy... method.
        if (favoriteRepository.findByUserIdAndProductId(userId, productId).isEmpty()) {
            throw new ResourceNotFoundException("Favorite entry not found for user " + userId + " and product " + productId);
        }
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        return ApiResponse.success("Product removed from favorites successfully", null);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public List<FavoriteDto> getFavoritesByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public boolean isFavorite(Long userId, Long productId) {
        // Corrected: Use isPresent() on the Optional returned by findBy...
        return favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    // --- Helper Methods ---
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + userId));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
    }
}