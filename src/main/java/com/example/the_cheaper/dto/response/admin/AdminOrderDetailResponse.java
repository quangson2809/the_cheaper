package com.example.the_cheaper.dto.response.admin;

import com.example.the_cheaper.dto.response.user.UserOrderItemResponse;
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
public class AdminOrderDetailResponse {
    private Long id;
    private BigDecimal finalTotal;
    private String status;

    private LocalDateTime createdAt;

    private String paymentMethodCode;
    private int paymentStatus;

    private String receiver;
    private String phone;
    private String location;

    private List<AdminOrderItemResponse> items;
}



