package com.example.the_cheaper.dto.response.admin;

import com.example.the_cheaper.entity.OrderStatus;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderOverviewResponse {
    private Long id;
    private BigDecimal finalTotal;
    private String status;
    private LocalDateTime createdAt;
    private int countItem;

    private String location;
    private String phone;

    private String paymentMethodCode;
    private int paymentStatus;
}



