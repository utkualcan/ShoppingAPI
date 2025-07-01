package org.utku.shoppingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utku.shoppingapi.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}