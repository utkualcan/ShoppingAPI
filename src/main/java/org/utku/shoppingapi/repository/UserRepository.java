package org.utku.shoppingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utku.shoppingapi.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}