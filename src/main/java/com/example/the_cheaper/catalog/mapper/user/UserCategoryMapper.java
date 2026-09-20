package com.example.the_cheaper.catalog.mapper.user;

import com.example.the_cheaper.catalog.dto.request.admin.AdminCategoryRequest;
import com.example.the_cheaper.catalog.dto.response.admin.AdminCategoryResponse;
import com.example.the_cheaper.catalog.dto.response.user.UserCategoryResponse;
import com.example.the_cheaper.catalog.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserCategoryMapper {
    UserCategoryResponse toResponse(CategoryEntity entity);
}


