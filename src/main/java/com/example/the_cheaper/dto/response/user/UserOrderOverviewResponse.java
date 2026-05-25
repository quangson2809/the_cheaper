package com.example.the_cheaper.dto.response.user;

import com.example.the_cheaper.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderOverviewResponse {
    private Long id;
    private OrderStatus status;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;
    private String paymentMethodCode;
}


