package com.example.the_cheaper.repository;

import com.example.the_cheaper.entity.BrandEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.the_cheaper.entity.MaterialEntity;

import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<MaterialEntity, Long> {
    Optional<MaterialEntity> findByName(String name);

    @Query("SELECT p FROM MaterialEntity p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<MaterialEntity> findMaterialByNameContainingIgnoreCase(
            @Param("name") String name,
            Pageable pageable);
}
