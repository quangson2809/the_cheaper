package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.request.admin.AdminRoleCreateRequest;
import com.example.the_cheaper.dto.request.admin.AdminRoleUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminRoleResponse;
import com.example.the_cheaper.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminRoleMapper {

    AdminRoleResponse toResponse(RoleEntity entity);

    @Mapping(target = "id", ignore = true)
    RoleEntity toEntity(AdminRoleCreateRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(AdminRoleUpdateRequest request, @MappingTarget RoleEntity entity);
}
