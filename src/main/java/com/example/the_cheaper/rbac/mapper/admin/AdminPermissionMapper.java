package com.example.the_cheaper.rbac.mapper.admin;

import com.example.the_cheaper.rbac.dto.request.admin.AdminPermissionCreateRequest;
import com.example.the_cheaper.rbac.dto.request.admin.AdminPermissionUpdateRequest;
import com.example.the_cheaper.rbac.dto.response.admin.AdminPermissionResponse;
import com.example.the_cheaper.rbac.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminPermissionMapper {

    AdminPermissionResponse toResponse(PermissionEntity entity);

    @Mapping(target = "id", ignore = true)
    PermissionEntity toEntity(AdminPermissionCreateRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(AdminPermissionUpdateRequest request, @MappingTarget PermissionEntity entity);
}
