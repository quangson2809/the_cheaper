package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.response.admin.AdminOrderItemResponse;
import com.example.the_cheaper.entity.OptionValueEntity;
import com.example.the_cheaper.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AdminOrderItemMapper {

    @Mapping(target = "productId", source = "variant.product.id")
    @Mapping(target = "productName", source = "variant.product.name")
    @Mapping(target = "thumbnail", expression = "java(entity.getVariant().getProduct().getThumbnail())")
    @Mapping(target = "optionNames", source = "variant.optionValues", qualifiedByName = "optionValuesToText")
    AdminOrderItemResponse toResponse(OrderItemEntity entity);

    @Named("optionValuesToText")
    default String optionValuesToText(List<OptionValueEntity> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream()
                .map(OptionValueEntity::getValue)
                .collect(Collectors.joining(", "));
    }
}
