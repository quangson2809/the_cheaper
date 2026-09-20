package com.example.the_cheaper.account.mapper.admin;

import com.example.the_cheaper.account.dto.response.admin.AdminAccountResponse;
import com.example.the_cheaper.account.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminAccountMapper {

    @Mapping(target = "role", source = "role.name")
    AdminAccountResponse toResponse(AccountEntity entity);
}
