package org.utku.shoppingapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.utku.shoppingapi.constants.AppConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing an item in a shopping cart.
 * This class manages individual products within a cart including:
 * - Product reference and quantity
 * - Unit price at the time of adding to cart
 * - Relationship with the parent cart
 * 
 * Each cart item represents a unique product in a cart with a specific quantity.
 * The unique constraint ensures no duplicate products in the same cart.
 */
@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    /**
     * Unique identifier for the cart item.
     * Auto-generated using database identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The cart this item belongs to.
     * Many-to-one relationship, lazily loaded.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @NotNull(message = "Cart cannot be null")
    private Cart cart;

    /**
     * The product in this cart item.
     * Many-to-one relationship, lazily loaded.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Product cannot be null")
    private Product product;

    /**
     * Quantity of the product in the cart.
     * Must be at least the minimum allowed quantity.
     */
    @Column(nullable = false)
    @NotNull(message = "Quantity cannot be null")
    @Min(value = AppConstants.MIN_QUANTITY, message = "Quantity must be at least " + AppConstants.MIN_QUANTITY)
    private Integer quantity;

    /**
     * Unit price of the product when added to cart.
     * Stored to maintain price consistency during cart session.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Unit price cannot be null")
    private BigDecimal unitPrice;

    /**
     * Timestamp when the cart item was created.
     * Automatically set on entity creation and never updated.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the cart item was last updated.
     * Automatically updated whenever the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Calculates the total price for this cart item.
     * 
     * @return Total price (unit price × quantity)
     */
    public BigDecimal getTotalPrice() {
        if (unitPrice == null) {
            throw new org.utku.shoppingapi.exception.ValidationException("Unit price cannot be null");
        }
        if (quantity == null) {
            throw new org.utku.shoppingapi.exception.ValidationException("Quantity cannot be null");
        }
        return unitPrice.multiply(new BigDecimal(quantity));
    }

    /**
     * Updates the quantity with validation.
     * Ensures the quantity meets minimum requirements.
     * 
     * @param newQuantity The new quantity (must be at least minimum allowed)
     * @throws IllegalArgumentException if quantity is invalid
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity < AppConstants.MIN_QUANTITY) {
            throw new IllegalArgumentException("Quantity must be at least " + AppConstants.MIN_QUANTITY);
        }
        this.quantity = newQuantity;
    }

    /**
     * Compares this cart item with another object for equality.
     * Two cart items are equal if they belong to the same cart and contain the same product.
     * 
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        CartItem cartItem = (CartItem) o;
        return cart != null && product != null &&
                cart.equals(cartItem.cart) &&
                product.equals(cartItem.product);
    }

    /**
     * Returns the hash code for this cart item.
     * Uses the class hash code to avoid issues with lazy loading.
     * 
     * @return Hash code for this object
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
