package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.response.admin.AdminProductImageResponse;
import com.example.the_cheaper.entity.ProductImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminProductImageMapper {
    AdminProductImageResponse toResponse(ProductImageEntity entity);

    List<AdminProductImageResponse> toResponseList(List<ProductImageEntity> entities);

    @Mapping(target = "name", source = "requestName")
    ProductImageEntity toEntity(String requestName);
}


