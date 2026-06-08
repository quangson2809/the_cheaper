package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.request.admin.AdminOptionAttributeRequest;
import com.example.the_cheaper.dto.request.admin.AdminOptionValueRequest;
import com.example.the_cheaper.dto.response.admin.AdminOptionAttributeResponse;
import com.example.the_cheaper.dto.response.admin.AdminOptionValueResponse;
import com.example.the_cheaper.entity.OptionAttributeEntity;
import com.example.the_cheaper.entity.OptionValueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminOptionAttributeMapper {

    // OptionAttributeEntity → AdminOptionAttributeResponse
    AdminOptionAttributeResponse toResponse(OptionAttributeEntity entity);

    // OptionValueEntity → AdminOptionValueResponse
    @Mapping(target = "attributeName", source = "optionAttribute.name")
    AdminOptionValueResponse toValueResponse(OptionValueEntity entity);

    // AdminOptionAttributeRequest → OptionAttributeEntity (create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "values", ignore = true)
    OptionAttributeEntity toEntity(AdminOptionAttributeRequest request);

    // update name only – values are handled manually in service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "values", ignore = true)
    void updateEntityFromRequest(AdminOptionAttributeRequest request,
                                 @MappingTarget OptionAttributeEntity entity);

    // AdminOptionValueRequest → OptionValueEntity (used when building value list)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "optionAttribute", ignore = true)
    @Mapping(target = "variants", ignore = true)
    OptionValueEntity toValueEntity(AdminOptionValueRequest request);
}
