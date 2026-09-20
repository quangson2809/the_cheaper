package com.example.the_cheaper.catalog.mapper.user;

import com.example.the_cheaper.catalog.dto.request.admin.AdminBrandRequest;
import com.example.the_cheaper.catalog.dto.response.admin.AdminBrandResponse;
import com.example.the_cheaper.catalog.dto.response.user.UserBrandResponse;
import com.example.the_cheaper.catalog.entity.BrandEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserBrandMapper {
    UserBrandResponse toResponse(BrandEntity entity);

}


