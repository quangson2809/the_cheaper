package com.example.the_cheaper.order.mapper.admin;

import com.example.the_cheaper.order.dto.response.admin.AdminOrderDetailResponse;
import com.example.the_cheaper.order.dto.response.admin.AdminOrderOverviewResponse;
import com.example.the_cheaper.order.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { AdminOrderItemMapper.class })
public interface AdminOrderMapper {

    @Mapping(target = "finalTotal", source = "finalAmount")
    @Mapping(target = "items", source = "items")
    AdminOrderDetailResponse toDetailResponse(OrderEntity entity);


    @Mapping(target = "finalTotal", source = "finalAmount")
    @Mapping(target = "countItem", expression = "java(entity.getCountItems())")
    AdminOrderOverviewResponse toOverviewResponse(OrderEntity entity);

    List<AdminOrderOverviewResponse> toOverviewResponseList(List<OrderEntity> entities);
}


