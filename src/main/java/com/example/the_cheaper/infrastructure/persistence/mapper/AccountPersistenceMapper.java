package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.Account;
import com.example.the_cheaper.infrastructure.persistence.entity.AccountEntity;
import com.example.the_cheaper.application.shared.CommonMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressPersistenceMapper.class, CommonMapper.class})
public interface AccountPersistenceMapper {

    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "passwordHash", source = "passwordHash")
    AccountEntity toEntity(Account account);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "passwordHash", source = "passwordHash")
    Account toDomain(AccountEntity entity);
}
