package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.request.admin.AdminBrandRequest;
import com.example.the_cheaper.dto.response.admin.AdminBrandResponse;
import com.example.the_cheaper.entity.BrandEntity;

import com.example.the_cheaper.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminBrandMapper {
    AdminBrandResponse toResponse(BrandEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    BrandEntity toEntity(AdminBrandRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntityFromRequest(AdminBrandRequest request, @MappingTarget BrandEntity entity);
}


