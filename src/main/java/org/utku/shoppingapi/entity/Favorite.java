package org.utku.shoppingapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a user's favorite product.
 * This class manages the many-to-many relationship between users and their favorite products.
 * 
 * Features include:
 * - Unique constraint to prevent duplicate favorites
 * - Timestamp tracking when the favorite was added
 * - Proper equals/hashCode implementation for entity comparison
 */
@Entity
@Table(name = "favorites",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    /**
     * Unique identifier for the favorite record.
     * Auto-generated using database identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who favorited the product.
     * Many-to-one relationship, lazily loaded.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;

    /**
     * The product that was favorited.
     * Many-to-one relationship, lazily loaded.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Product cannot be null")
    private Product product;

    /**
     * Timestamp when the favorite was created.
     * Automatically set on entity creation and never updated.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Compares this favorite with another object for equality.
     * Two favorites are equal if they have the same user and product.
     * 
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Favorite)) return false;
        Favorite favorite = (Favorite) o;
        return user != null && product != null &&
                user.equals(favorite.user) &&
                product.equals(favorite.product);
    }

    /**
     * Returns the hash code for this favorite.
     * Uses the class hash code to avoid issues with lazy loading.
     * 
     * @return Hash code for this object
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Returns a string representation of this favorite.
     * Includes ID, user ID, product ID, and creation timestamp.
     * 
     * @return String representation of the favorite
     */
    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", productId=" + (product != null ? product.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
