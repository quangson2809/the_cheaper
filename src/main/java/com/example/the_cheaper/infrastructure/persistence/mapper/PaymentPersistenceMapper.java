package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.Payment;
import com.example.the_cheaper.infrastructure.persistence.entity.PaymentEntity;
import com.example.the_cheaper.application.shared.CommonMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CommonMapper.class})
public interface PaymentPersistenceMapper {

    @Mapping(target = "orderId", source = "order.id")
    Payment toDomain(PaymentEntity entity);

    @Mapping(target = "order.id", source = "orderId")
    PaymentEntity toEntity(Payment domain);
}
