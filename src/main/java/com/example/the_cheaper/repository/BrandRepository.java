package com.example.the_cheaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.entity.BrandEntity;

public interface BrandRepository extends JpaRepository<BrandEntity, Long> {
    boolean existsByName(String name);
}
