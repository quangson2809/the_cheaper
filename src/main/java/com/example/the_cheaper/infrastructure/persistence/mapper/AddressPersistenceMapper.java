package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.Address;
import com.example.the_cheaper.infrastructure.persistence.entity.AddressEntity;
import com.example.the_cheaper.application.shared.CommonMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CommonMapper.class})
public interface AddressPersistenceMapper {

    Address toDomain(AddressEntity entity);

    AddressEntity toEntity(Address domain);
}
