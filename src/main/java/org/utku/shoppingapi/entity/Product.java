package org.utku.shoppingapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Ürün adı boş olamaz")
    @Size(min = 2, max = 255, message = "Ürün adı 2-255 karakter arası olmalıdır")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Açıklama en fazla 1000 karakter olabilir")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Fiyat boş olamaz")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fiyat sıfırdan büyük olmalıdır")
    private BigDecimal price;

    @Column(nullable = false)
    @NotNull(message = "Stok miktarı boş olamaz")
    @Min(value = 0, message = "Stok miktarı negatif olamaz")
    private Integer stockQuantity;

    @Column(length = 100)
    private String category;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public boolean isAvailable() {
        return active && isInStock();
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Azaltılacak miktar pozitif olmalıdır");
        }
        if (stockQuantity < quantity) {
            throw new IllegalStateException("Yetersiz stok");
        }
        this.stockQuantity -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Artırılacak miktar pozitif olmalıdır");
        }
        this.stockQuantity += quantity;
    }
}