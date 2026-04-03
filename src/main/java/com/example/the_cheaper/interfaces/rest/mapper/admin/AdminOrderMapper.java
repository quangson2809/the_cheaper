package com.example.the_cheaper.interfaces.rest.mapper.admin;

import com.example.the_cheaper.domain.model.Order;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.OrderResponse;
import com.example.the_cheaper.application.shared.CommonMapper;
import com.example.the_cheaper.interfaces.rest.mapper.user.OrderItemMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, CommonMapper.class})
public interface AdminOrderMapper {

    OrderResponse toResponse(Order domain);
}
