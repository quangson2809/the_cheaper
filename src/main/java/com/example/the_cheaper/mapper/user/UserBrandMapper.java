package com.example.the_cheaper.mapper.user;

import com.example.the_cheaper.dto.request.admin.AdminBrandRequest;
import com.example.the_cheaper.dto.response.admin.AdminBrandResponse;
import com.example.the_cheaper.dto.response.user.UserBrandResponse;
import com.example.the_cheaper.entity.BrandEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserBrandMapper {
    UserBrandResponse toResponse(BrandEntity entity);

}


