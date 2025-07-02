package org.utku.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.utku.shoppingapi.constants.AppConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a shopping cart in the e-commerce system.
 * This class manages collections of products that users intend to purchase.
 * 
 * Features include:
 * - Association with users or guest sessions
 * - Collection of cart items with quantities
 * - Automatic total price calculation
 * - Utility methods for cart management
 */
@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    /**
     * Unique identifier for the cart.
     * Auto-generated using database identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who owns this cart.
     * One-to-one relationship, lazily loaded.
     * Can be null for guest carts.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Session identifier for guest carts.
     * Used to associate carts with anonymous users before login.
     */
    @Column(length = AppConstants.MAX_SESSION_ID_LENGTH)
    private String sessionId;

    /**
     * List of items in the cart.
     * One-to-many relationship with cascade operations and orphan removal.
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    /**
     * Timestamp when the cart was created.
     * Automatically set on entity creation and never updated.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the cart was last updated.
     * Automatically updated whenever the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Calculates the total price of all items in the cart.
     * Sums up the total price of each cart item.
     * 
     * @return Total price as BigDecimal, or ZERO if cart is empty
     */
    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the total number of items in the cart.
     * Sums up the quantities of all cart items.
     * 
     * @return Total item count as integer
     */
    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Checks if the cart is empty.
     * 
     * @return true if cart has no items, false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
