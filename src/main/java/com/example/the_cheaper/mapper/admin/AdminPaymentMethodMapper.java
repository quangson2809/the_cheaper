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
    PaymentMethodResponse toResponse(PaymentMethodEntity entity);

}
