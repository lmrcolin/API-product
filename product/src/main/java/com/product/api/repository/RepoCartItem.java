package com.product.api.repository;

import com.product.api.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoCartItem extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUserId(Long userId);
}
