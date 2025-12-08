package com.product.api.repository;

import com.product.api.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RepoCartItem extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUserId(Long userId);

    @Query("SELECT c FROM CartItem c WHERE c.userId = :userId AND c.product.product_id = :productId")
    Optional<CartItem> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Integer productId);

    Optional<CartItem> findByIdAndUserId(Long id, Long userId);

    void deleteAllByUserId(Long userId);
}
