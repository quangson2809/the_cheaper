package com.example.the_cheaper.domain.repository;

import com.example.the_cheaper.domain.model.Cart;
import java.util.Optional;

public interface CartRepository extends BaseRepository<Cart, Long> {
    Optional<Cart> findByAccountId(Long accountId);
}

