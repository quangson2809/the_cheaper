package com.example.the_cheaper.catalog.mapper.user;

import com.example.the_cheaper.catalog.dto.response.user.UserOptionAttributeResponse;
import com.example.the_cheaper.catalog.dto.response.user.UserOptionValueResponse;
import com.example.the_cheaper.catalog.entity.OptionAttributeEntity;
import com.example.the_cheaper.catalog.entity.OptionValueEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserOptionAttributeMapper {

    UserOptionAttributeResponse toResponse(OptionAttributeEntity entity);

    UserOptionValueResponse toValueResponse(OptionValueEntity entity);
}
