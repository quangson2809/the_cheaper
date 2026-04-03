package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.OptionValue;
import com.example.the_cheaper.infrastructure.persistence.entity.OptionValueEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionValuePersistenceMapper {

    OptionValue toDomain(OptionValueEntity entity);

    OptionValueEntity toEntity(OptionValue domain);
}
