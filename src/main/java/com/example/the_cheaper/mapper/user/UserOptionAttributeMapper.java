package com.example.the_cheaper.mapper.user;

import com.example.the_cheaper.dto.response.user.UserOptionAttributeResponse;
import com.example.the_cheaper.dto.response.user.UserOptionValueResponse;
import com.example.the_cheaper.entity.OptionAttributeEntity;
import com.example.the_cheaper.entity.OptionValueEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserOptionAttributeMapper {

    UserOptionAttributeResponse toResponse(OptionAttributeEntity entity);

    UserOptionValueResponse toValueResponse(OptionValueEntity entity);
}
