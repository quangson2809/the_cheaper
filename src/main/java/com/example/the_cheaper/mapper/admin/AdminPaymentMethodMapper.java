package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.request.admin.AdminCreatePaymentMethodRequest;
import com.example.the_cheaper.dto.request.admin.AdminUpdatePaymentMethodRequest;
import com.example.the_cheaper.dto.response.common.PaymentMethodResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.PaymentMethodEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminPaymentMethodMapper {
    @Mapping(target = "status", expression = "java(getStatus(entity))")
    PaymentMethodResponse toResponse(PaymentMethodEntity entity);

    default String getStatus(PaymentMethodEntity entity) {
        return entity.getStatus() == 1 ? "active" : "inactive";
    }
}
