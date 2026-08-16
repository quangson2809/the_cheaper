package com.example.the_cheaper.mapper.user;

import com.example.the_cheaper.dto.request.user.UserAddressCreateRequest;
import com.example.the_cheaper.dto.request.user.UserAddressUpdateRequest;
import com.example.the_cheaper.dto.response.user.UserAddressResponse;
import com.example.the_cheaper.entity.AddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {

    UserAddressResponse toResponse(AddressEntity entity);

    List<UserAddressResponse> toResponseList(List<AddressEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    AddressEntity toEntity(UserAddressCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    void updateEntity(@MappingTarget AddressEntity entity, UserAddressUpdateRequest request);
}
