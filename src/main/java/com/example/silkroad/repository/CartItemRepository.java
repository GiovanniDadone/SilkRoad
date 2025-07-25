package com.example.silkroad.repository;

import com.example.silkroad.domain.CartItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CartItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {}
