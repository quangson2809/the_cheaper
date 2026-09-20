package com.example.the_cheaper.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.catalog.entity.OptionAttributeEntity;
import com.example.the_cheaper.catalog.entity.OptionValueEntity;

import java.util.List;

public interface OptionValueRepository extends JpaRepository<OptionValueEntity, Long> {
    List<OptionValueEntity> findByOptionAttribute(OptionAttributeEntity optionAttribute);
    boolean existsByValueAndOptionAttribute(String value, OptionAttributeEntity optionAttribute);
}
