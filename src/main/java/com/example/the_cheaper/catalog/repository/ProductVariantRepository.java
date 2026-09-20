package com.example.the_cheaper.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.catalog.entity.ProductVariantEntity;

public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, Long> {
}
