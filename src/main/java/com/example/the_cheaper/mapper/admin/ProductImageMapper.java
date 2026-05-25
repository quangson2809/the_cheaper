package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.request.admin.AdminCategoryRequest;
import com.example.the_cheaper.dto.response.admin.AdminCategoryResponse;
import com.example.the_cheaper.dto.response.admin.AdminProductImageResponse;
import com.example.the_cheaper.entity.CategoryEntity;
import com.example.the_cheaper.entity.ProductImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    AdminProductImageResponse toResponse(ProductImageEntity entity);

    List<AdminProductImageResponse> toResponseList(List<ProductImageEntity> entities);

    @Mapping(target = "name", source = "requestName")
    ProductImageEntity toEntity(String requestName);
}


