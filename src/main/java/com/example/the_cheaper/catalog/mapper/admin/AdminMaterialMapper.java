package com.example.the_cheaper.catalog.mapper.admin;

import com.example.the_cheaper.catalog.dto.request.admin.AdminMaterialRequest;
import com.example.the_cheaper.catalog.dto.response.admin.AdminMaterialResponse;
import com.example.the_cheaper.catalog.entity.MaterialEntity;

import com.example.the_cheaper.catalog.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminMaterialMapper {
    AdminMaterialResponse toResponse(MaterialEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    MaterialEntity toEntity(AdminMaterialRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntityFromRequest(AdminMaterialRequest request, @MappingTarget MaterialEntity entity);
}


