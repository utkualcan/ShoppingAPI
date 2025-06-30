package org.utku.shoppingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utku.shoppingapi.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
