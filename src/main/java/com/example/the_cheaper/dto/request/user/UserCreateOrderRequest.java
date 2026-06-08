package com.example.the_cheaper.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateOrderRequest {
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private Long paymentMethodId;

    private String receiver;
    private String location;
    private String phone;
}
