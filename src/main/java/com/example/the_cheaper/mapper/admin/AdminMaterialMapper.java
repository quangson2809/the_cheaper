package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.request.admin.AdminMaterialRequest;
import com.example.the_cheaper.dto.response.admin.AdminMaterialResponse;
import com.example.the_cheaper.entity.MaterialEntity;

import com.example.the_cheaper.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminMaterialMapper {
    @Mapping(target = "status", expression = "java(getStatus(entity))")
    AdminMaterialResponse toResponse(MaterialEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    MaterialEntity toEntity(AdminMaterialRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntityFromRequest(AdminMaterialRequest request, @MappingTarget MaterialEntity entity);

    default String getStatus(MaterialEntity entity) {
        if (entity.getStatus() == 0) {
            return "Deleted";
        } else{
            return "Active";
        }
    }
}


