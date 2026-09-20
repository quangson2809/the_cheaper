package com.example.the_cheaper.account.mapper.user;

import com.example.the_cheaper.account.dto.response.user.UserAccountResponse;
import com.example.the_cheaper.account.entity.AccountEntity;
import com.example.the_cheaper.rbac.entity.AccountRoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", uses = {UserAddressMapper.class})
public interface UserAccountMapper {

    @Mapping(target = "role", source = "accountRoles")
    UserAccountResponse toResponse(AccountEntity entity);

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
