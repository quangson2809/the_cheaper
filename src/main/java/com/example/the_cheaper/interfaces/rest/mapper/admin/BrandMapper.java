package com.example.the_cheaper.interfaces.rest.mapper.admin;

import com.example.the_cheaper.domain.model.Brand;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.BrandResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    BrandResponse toResponse(Brand domain);
}
