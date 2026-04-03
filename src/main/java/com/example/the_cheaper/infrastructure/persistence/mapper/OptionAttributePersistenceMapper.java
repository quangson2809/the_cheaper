package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.OptionAttribute;
import com.example.the_cheaper.infrastructure.persistence.entity.OptionAttributeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionAttributePersistenceMapper {

    OptionAttribute toDomain(OptionAttributeEntity entity);

    OptionAttributeEntity toEntity(OptionAttribute domain);
}
