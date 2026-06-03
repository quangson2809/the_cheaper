package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.response.admin.AdminOrderDetailResponse;
import com.example.the_cheaper.dto.response.admin.AdminOrderOverviewResponse;
import com.example.the_cheaper.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { AdminOrderItemMapper.class })
public interface AdminOrderMapper {

    @Mapping(target = "finalTotal", source = "finalAmount")
    @Mapping(target = "paymentStatus", expression = "java(entity.getPaymentStatus())")
    @Mapping(target = "items", source = "items")
    AdminOrderDetailResponse toDetailResponse(OrderEntity entity);

    @Mapping(target = "finalTotal", source = "finalAmount")
    @Mapping(target = "countItem", expression = "java(entity.getCountItems())")
    @Mapping(target = "paymentStatus", expression = "java(entity.getPaymentStatus())")
    AdminOrderOverviewResponse toOverviewResponse(OrderEntity entity);

    List<AdminOrderOverviewResponse> toOverviewResponseList(List<OrderEntity> entities);
}


