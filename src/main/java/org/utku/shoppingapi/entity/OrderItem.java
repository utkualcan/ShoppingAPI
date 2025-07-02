package org.utku.shoppingapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing an item within an order.
 * This class contains information about a specific product in an order including:
 * - Reference to the parent order
 * - Product details and quantity
 * - Unit price at the time of order
 * 
 * Order items are created when an order is placed and capture the product price
 * at that moment to maintain historical accuracy.
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * Unique identifier for the order item.
     * Auto-generated using database identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The order this item belongs to.
     * Many-to-one relationship, lazily loaded.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * The product being ordered.
     * Many-to-one relationship, lazily loaded.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Quantity of the product ordered.
     * Must be a positive integer.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Unit price of the product at the time of order.
     * Stored to maintain historical pricing accuracy.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Calculates the total price for this order item.
     * 
     * @return Total price (unit price × quantity)
     */
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }
}