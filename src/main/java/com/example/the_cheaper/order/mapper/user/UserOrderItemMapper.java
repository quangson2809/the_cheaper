package com.example.the_cheaper.order.mapper.user;

import com.example.the_cheaper.order.dto.response.user.UserOrderItemResponse;
import com.example.the_cheaper.catalog.entity.OptionValueEntity;
import com.example.the_cheaper.order.entity.OrderItemEntity;

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
    @Mapping(target = "thumbnailUrl", expression = "java(entity.getVariant().getProduct().getThumbnail())")
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


