package com.example.the_cheaper.interfaces.rest.mapper.user;

import com.example.the_cheaper.application.command.AddToCartCommand;
import com.example.the_cheaper.application.command.UpdateCartItemCommand;
import com.example.the_cheaper.interfaces.rest.dto.request.user.CartRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "userId")
    AddToCartCommand toAddCommand(Long userId, CartRequest request);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "cartItemId", source = "cartItemId")
    @Mapping(target = "quantity", source = "request.quantity")
    UpdateCartItemCommand toUpdateCommand(Long userId, Long cartItemId, CartRequest request);
}
