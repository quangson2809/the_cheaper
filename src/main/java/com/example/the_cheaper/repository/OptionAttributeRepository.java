package com.example.the_cheaper.repository;

import com.example.the_cheaper.entity.BrandEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.entity.OptionAttributeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OptionAttributeRepository extends JpaRepository<OptionAttributeEntity, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT p FROM OptionAttributeEntity p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<OptionAttributeEntity> findOptionAttributeByNameContainingIgnoreCase(
            @Param("name") String name,
            Pageable pageable);
}
