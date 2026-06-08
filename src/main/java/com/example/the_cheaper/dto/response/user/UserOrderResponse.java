package com.example.the_cheaper.dto.response.user;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.the_cheaper.entity.OrderStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderResponse {
    private Long id;
    private OrderStatus status;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;
    private String receiver;
    private String location;
    private String paymentMethodCode;
    private List<UserOrderItemResponse> items;
}


