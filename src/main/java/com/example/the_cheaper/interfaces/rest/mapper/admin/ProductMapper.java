package com.example.the_cheaper.interfaces.rest.mapper.admin;

import com.example.the_cheaper.application.command.*;
import com.example.the_cheaper.interfaces.rest.dto.request.admin.*;
import com.example.the_cheaper.domain.model.Product;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.*;
import com.example.the_cheaper.application.shared.CommonMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CommonMapper.class})
public interface ProductMapper {

    ProductResponse toResponse(Product domain);

    CreateProductCommand toCommand(ProductRequest request);

    @Mapping(target = "id", source = "id")
    UpdateProductCommand toCommand(Long id, ProductRequest request);

    VariantCommand toCommand(VariantRequest request);

    OptionAttributeCommand toCommand(OptionAttributeRequest request);

    OptionValueCommand toCommand(OptionValueRequest request);

    CreateCategoryCommand toCommand(CategoryRequest request);

    @Mapping(target = "id", source = "id")
    UpdateCategoryCommand toCommand(Long id, CategoryRequest request);

    CreateBrandCommand toCommand(BrandRequest request);

    @Mapping(target = "id", source = "id")
    UpdateBrandCommand toCommand(Long id, BrandRequest request);
}
