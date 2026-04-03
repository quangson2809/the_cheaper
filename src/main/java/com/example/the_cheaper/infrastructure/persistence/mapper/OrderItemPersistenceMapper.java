package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.OrderItem;
import com.example.the_cheaper.infrastructure.persistence.entity.OrderItemEntity;
import com.example.the_cheaper.application.shared.CommonMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CommonMapper.class})
public interface OrderItemPersistenceMapper {

    OrderItem toDomain(OrderItemEntity entity);

    OrderItemEntity toEntity(OrderItem domain);
}
