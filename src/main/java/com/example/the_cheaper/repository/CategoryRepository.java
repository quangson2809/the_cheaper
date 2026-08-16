package com.example.the_cheaper.repository;

import com.example.the_cheaper.entity.MaterialEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.entity.CategoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    boolean existsByName(String name);

    @Query("SELECT c FROM CategoryEntity c WHERE c.status = 1")
    List<CategoryEntity> findByStatus();

    @Query("SELECT p FROM CategoryEntity p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<CategoryEntity> findCategoryByNameContainingIgnoreCase(
            @Param("name") String name,
            Pageable pageable);
}
