package com.example.the_cheaper.mapper.user;

import com.example.the_cheaper.dto.response.user.UserOrderItemResponse;
import com.example.the_cheaper.entity.OptionValueEntity;
import com.example.the_cheaper.entity.OrderItemEntity;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserOrderItemMapper {

    @Mapping(target = "productId", source = "variant.product.id")
    @Mapping(target = "productName", source = "variant.product.name")
    @Mapping(target = "optionValue", source = "variant.optionValues", qualifiedByName = "optionValuesToText")
    @Mapping(target = "unitPrice", expression = "java(entity.calculateUnitPrice())")
    UserOrderItemResponse toResponse(OrderItemEntity entity);

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


