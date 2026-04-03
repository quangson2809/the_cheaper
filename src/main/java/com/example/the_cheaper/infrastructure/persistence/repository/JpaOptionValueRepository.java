package com.example.the_cheaper.infrastructure.persistence.repository;

import com.example.the_cheaper.infrastructure.persistence.entity.OptionValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOptionValueRepository extends JpaRepository<OptionValueEntity, Long> {
}
