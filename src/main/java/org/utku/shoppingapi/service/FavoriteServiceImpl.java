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

/**
 * Implementation of FavoriteService.
 * Handles all business logic and security for managing user favorites.
 */
@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {
    /**
     * Repository for favorite data access.
     */
    private final FavoriteRepository favoriteRepository;
    /**
     * Repository for user data access.
     */
    private final UserRepository userRepository;
    /**
     * Repository for product data access.
     */
    private final ProductRepository productRepository;
    /**
     * Mapper for converting entities to DTOs.
     */
    private final EntityMapper mapper;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, UserRepository userRepository, ProductRepository productRepository, EntityMapper mapper) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    /**
     * Adds a product to a user's favorites list. Access is restricted to the user or ADMIN.
     * @param userId User ID
     * @param productId Product ID
     * @return Newly created FavoriteDto
     */
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

    /**
     * Removes a product from a user's favorites list. Access is restricted to the user or ADMIN.
     * @param userId User ID
     * @param productId Product ID
     * @return Success response message
     */
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

    /**
     * Retrieves all favorites for a specific user. Access is restricted to the user or ADMIN.
     * @param userId User ID
     * @return List of FavoriteDto objects
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public List<FavoriteDto> getFavoritesByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a product is in a user's favorites. Access is restricted to the user or ADMIN.
     * @param userId User ID
     * @param productId Product ID
     * @return true if product is favorited, false otherwise
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public boolean isFavorite(Long userId, Long productId) {
        // Corrected: Use isPresent() on the Optional returned by findBy...
        return favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    // --- Helper Methods ---
    /**
     * Finds a user entity by its ID.
     * @param userId User ID
     * @return User entity
     * @throws ResourceNotFoundException if user not found
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + userId));
    }
    /**
     * Finds a product entity by its ID.
     * @param productId Product ID
     * @return Product entity
     * @throws ResourceNotFoundException if product not found
     */
    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + productId));
    }
}