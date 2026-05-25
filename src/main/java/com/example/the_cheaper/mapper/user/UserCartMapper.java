package com.example.the_cheaper.mapper.user;

import com.example.the_cheaper.dto.response.user.UserCartItemResponse;
import com.example.the_cheaper.dto.response.user.UserCartOverviewResponse;
import com.example.the_cheaper.dto.response.user.UserCartResponse;
import com.example.the_cheaper.dto.response.user.UserOrderItemResponse;
import com.example.the_cheaper.entity.CartEntity;
import com.example.the_cheaper.entity.CartItemEntity;
import com.example.the_cheaper.entity.OptionValueEntity;
import com.example.the_cheaper.entity.OrderItemEntity;
import jakarta.persistence.ManyToOne;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserCartMapper {

    @Mapping(target = "productId", source = "variant.product.id")
    @Mapping(target = "productName", source = "variant.product.name")
    @Mapping(target = "price", expression = "java(entity.getVariant().getOverridePrice())")
    @Mapping(target = "optionNames", source = "variant.optionValues", qualifiedByName = "optionValuesToText")
    UserCartItemResponse toResponse(CartItemEntity entity);

    CartItemEntity toCartItemEntity(Long variantId, int quantity);

    @Named("optionValuesToText")
    default List<String> optionValuesToText(List<OptionValueEntity> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream()
                .map(OptionValueEntity::getValue)
                .toList();
    }

    List<UserCartItemResponse> toResponseList(List<OrderItemEntity> entities);

    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalPrice", expression = "java(entity.calculateTotalPrice())")
    UserCartResponse toResponse(CartEntity entity);

    @Mapping(target = "countItems", expression = "java(entity.getTotalQuantity())")

    UserCartOverviewResponse toOverviewResponse(CartEntity entity);

}


