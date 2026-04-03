package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.ProductVariant;
import com.example.the_cheaper.infrastructure.persistence.entity.ProductVariantEntity;
import com.example.the_cheaper.application.shared.CommonMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CommonMapper.class, OptionValuePersistenceMapper.class})
public interface ProductVariantPersistenceMapper {

    ProductVariant toDomain(ProductVariantEntity entity);

    ProductVariantEntity toEntity(ProductVariant domain);
}
