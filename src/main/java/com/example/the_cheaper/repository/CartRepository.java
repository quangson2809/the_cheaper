package com.example.the_cheaper.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.entity.CartEntity;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByAccountId(Long accountId);
}
