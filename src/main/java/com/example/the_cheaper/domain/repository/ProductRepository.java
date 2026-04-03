package com.example.the_cheaper.domain.repository;

import com.example.the_cheaper.domain.model.Product;

public interface ProductRepository extends BaseRepository<Product, Long> {
    boolean existsByName(String name);
    boolean existsById(Long id);
}
