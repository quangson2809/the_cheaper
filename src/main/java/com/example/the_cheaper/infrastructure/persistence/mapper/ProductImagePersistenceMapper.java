package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.ProductImage;
import com.example.the_cheaper.infrastructure.persistence.entity.ProductImageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImagePersistenceMapper {

    ProductImage toDomain(ProductImageEntity entity);

    ProductImageEntity toEntity(ProductImage domain);
}
