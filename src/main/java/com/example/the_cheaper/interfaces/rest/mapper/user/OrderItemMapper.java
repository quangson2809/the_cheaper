package com.example.the_cheaper.interfaces.rest.mapper.user;

import com.example.the_cheaper.domain.model.OrderItem;
import com.example.the_cheaper.interfaces.rest.dto.response.user.OrderItemResponse;
import com.example.the_cheaper.application.shared.CommonMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CommonMapper.class})
public interface OrderItemMapper {

    @Mapping(target = "variantId", source = "variantId")
    @Mapping(target = "unitPrice", source = "unitPrice")
    OrderItemResponse toResponse(OrderItem domain);
}
