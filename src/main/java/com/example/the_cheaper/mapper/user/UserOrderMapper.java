package com.example.the_cheaper.mapper.user;

import com.example.the_cheaper.dto.response.user.UserOrderResponse;
import com.example.the_cheaper.entity.AddressEntity;
import com.example.the_cheaper.entity.CartItemEntity;
import com.example.the_cheaper.entity.OrderEntity;

import com.example.the_cheaper.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserOrderItemMapper.class })
public interface UserOrderMapper {

    @Mapping(target = "receiver", source = "receiver")
    @Mapping(target = "location", source = "location")
    UserOrderResponse toResponse(OrderEntity entity);

    OrderEntity toOrderEntity(AddressEntity address, List<OrderItemEntity> items, String paymentMethodCode);

    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "price", source = "variant.overridePrice")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "variant", ignore = true)
    OrderItemEntity toOrderItemEntity(CartItemEntity entity);


}


