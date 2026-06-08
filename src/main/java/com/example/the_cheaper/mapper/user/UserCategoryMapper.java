package com.example.the_cheaper.mapper.user;

import com.example.the_cheaper.dto.request.admin.AdminCategoryRequest;
import com.example.the_cheaper.dto.response.admin.AdminCategoryResponse;
import com.example.the_cheaper.dto.response.user.UserCategoryResponse;
import com.example.the_cheaper.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserCategoryMapper {
    UserCategoryResponse toResponse(CategoryEntity entity);
}


