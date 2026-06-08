package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.request.admin.AdminCategoryRequest;
import com.example.the_cheaper.dto.response.admin.AdminCategoryResponse;
import com.example.the_cheaper.entity.CategoryEntity;

import com.example.the_cheaper.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminCategoryMapper {
    @Mapping(target = "status", expression = "java(getStatus(entity))")
    AdminCategoryResponse toResponse(CategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    CategoryEntity toEntity(AdminCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntityFromRequest(AdminCategoryRequest request, @MappingTarget CategoryEntity entity);

    default String getStatus(CategoryEntity entity) {
        if (entity.getStatus() == 0) {
            return "Deleted";
        } else{
            return "Active";
        }
    }
}


