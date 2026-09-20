package com.example.the_cheaper.account.mapper.user;

import com.example.the_cheaper.account.dto.response.user.UserAccountResponse;
import com.example.the_cheaper.account.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserAddressMapper.class})
public interface UserAccountMapper {

    @Mapping(target = "role", source = "role.name")
    UserAccountResponse toResponse(AccountEntity entity);
}
