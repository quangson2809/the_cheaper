package com.example.the_cheaper.interfaces.rest.mapper.admin;

import com.example.the_cheaper.domain.model.Category;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category domain);
}
