package com.example.the_cheaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.entity.ProductVariantEntity;

public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, Long> {
}
