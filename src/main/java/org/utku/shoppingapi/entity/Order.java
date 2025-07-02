package org.utku.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an order in the e-commerce system.
 * This class contains all order-related information including:
 * - Association with the user who placed the order
 * - Collection of order items (products and quantities)
 * - Total amount and order timestamp
 * 
 * Orders are created from shopping carts and represent completed purchases.
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * Unique identifier for the order.
     * Auto-generated using database identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who placed this order.
     * Many-to-one relationship, lazily loaded.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * List of items in this order.
     * One-to-many relationship with cascade operations and orphan removal.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Total amount for the order with 2 decimal places precision.
     * Calculated from the sum of all order item totals.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Timestamp when the order was placed.
     * Automatically set on entity creation and never updated.
     */
    @CreationTimestamp
    @Column(name = "ordered_at", updatable = false)
    private LocalDateTime orderedAt;

    /**
     * Adds an item to the order.
     * Sets the bidirectional relationship between order and order item.
     * 
     * @param item The order item to add
     */
    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }
    
    /**
     * Calculates the total amount of all items in the order.
     * Sums up the total price of each order item.
     * 
     * @return Total amount as BigDecimal, or ZERO if order is empty
     */
    public BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Updates the total amount based on current items.
     * Should be called after adding/removing items.
     */
    public void updateTotalAmount() {
        this.totalAmount = calculateTotalAmount();
    }
}