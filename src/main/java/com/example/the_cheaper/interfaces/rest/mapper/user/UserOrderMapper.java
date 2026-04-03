package com.example.the_cheaper.interfaces.rest.mapper.user;

import com.example.the_cheaper.domain.model.Order;
import com.example.the_cheaper.interfaces.rest.dto.response.user.OrderResponse;
import com.example.the_cheaper.application.command.PlaceOrderCommand;
import com.example.the_cheaper.application.command.UpdateOrderStatusCommand;
import com.example.the_cheaper.interfaces.rest.dto.request.admin.OrderStatusUpdateRequest;
import com.example.the_cheaper.interfaces.rest.dto.request.user.CheckoutRequest;
import com.example.the_cheaper.application.shared.CommonMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, CommonMapper.class})
public interface UserOrderMapper {

    OrderResponse toResponse(Order domain);

    @Mapping(target = "userId", source = "userId")
    PlaceOrderCommand toCommand(Long userId, CheckoutRequest request);

    @Mapping(target = "id", source = "orderId")
    UpdateOrderStatusCommand toCommand(Long orderId, OrderStatusUpdateRequest request);
}
