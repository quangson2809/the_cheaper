package com.example.the_cheaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    boolean existsByName(String name);
}
