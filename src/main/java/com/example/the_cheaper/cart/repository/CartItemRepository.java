package com.example.the_cheaper.cart.repository;

import com.example.the_cheaper.cart.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    Optional<CartItemEntity> findByIdAndCartId(Long id, Long cartId);

    boolean existsByCartIdAndVariantId(Long cartId, Long variantId);

    Optional<CartItemEntity> findByCartIdAndVariantId(Long cartId, Long variantId);
}
