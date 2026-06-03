package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.response.admin.AdminAccountResponse;
import com.example.the_cheaper.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminAccountMapper {

    @Mapping(target = "role", source = "role.name")
    @Mapping(target = "status", expression = "java(getStatus(entity))")
    AdminAccountResponse toResponse(AccountEntity entity);

    default String getStatus(AccountEntity entity) {
        return entity.getStatus() == 1 ? "active" : "inactive";
    }
}
