package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.Brand;
import com.example.the_cheaper.infrastructure.persistence.entity.BrandEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandPersistenceMapper {

    Brand toDomain(BrandEntity entity);

    BrandEntity toEntity(Brand domain);
}
