package com.example.the_cheaper.payment.mapper.admin;

import com.example.the_cheaper.payment.dto.request.admin.AdminCreatePaymentMethodRequest;
import com.example.the_cheaper.payment.dto.request.admin.AdminUpdatePaymentMethodRequest;
import com.example.the_cheaper.payment.dto.response.PaymentMethodResponse;
import com.example.the_cheaper.account.entity.AccountEntity;
import com.example.the_cheaper.payment.entity.PaymentMethodEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminPaymentMethodMapper {
    PaymentMethodResponse toResponse(PaymentMethodEntity entity);

}
