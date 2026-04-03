package com.example.the_cheaper.infrastructure.persistence.repository;

import com.example.the_cheaper.infrastructure.persistence.entity.OptionAttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOptionAttributeRepository extends JpaRepository<OptionAttributeEntity, Long> {
}
