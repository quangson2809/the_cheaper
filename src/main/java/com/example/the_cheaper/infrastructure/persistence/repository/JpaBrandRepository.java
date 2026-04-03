package com.example.the_cheaper.infrastructure.persistence.repository;

import com.example.the_cheaper.infrastructure.persistence.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaBrandRepository extends JpaRepository<BrandEntity, Long> {
    boolean existsByName(String name);
}
