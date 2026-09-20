package com.example.the_cheaper.catalog.mapper.admin;

import com.example.the_cheaper.catalog.dto.request.admin.AdminCategoryRequest;
import com.example.the_cheaper.catalog.dto.response.admin.AdminCategoryResponse;
import com.example.the_cheaper.catalog.entity.CategoryEntity;

import com.example.the_cheaper.catalog.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminCategoryMapper {
    AdminCategoryResponse toResponse(CategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    CategoryEntity toEntity(AdminCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntityFromRequest(AdminCategoryRequest request, @MappingTarget CategoryEntity entity);
}

