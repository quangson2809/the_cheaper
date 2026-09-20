package com.example.the_cheaper.account.mapper.admin;

import com.example.the_cheaper.account.dto.response.admin.AdminAccountResponse;
import com.example.the_cheaper.account.entity.AccountEntity;
import com.example.the_cheaper.rbac.entity.AccountRoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface AdminAccountMapper {

    @Mapping(target = "role", source = "accountRoles")
    AdminAccountResponse toResponse(AccountEntity entity);

    default String mapRoleName(List<AccountRoleEntity> accountRoles) {
        if (accountRoles == null) {
            return null;
        }

        return accountRoles.stream()
                .filter(Objects::nonNull)
                .map(AccountRoleEntity::getRole)
                .filter(Objects::nonNull)
                .map(role -> role.getName())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
