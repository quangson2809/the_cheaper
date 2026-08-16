package com.example.the_cheaper.repository;

import com.example.the_cheaper.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.entity.BrandEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrandRepository extends JpaRepository<BrandEntity, Long> {
    boolean existsByName(String name);

    @Query("select b from BrandEntity b where b.status = 1")
    List<BrandEntity> findByStatus();

    @Query("SELECT p FROM BrandEntity p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<BrandEntity> findBrandByNameContainingIgnoreCase(
            @Param("name") String name,
            Pageable pageable);
}
