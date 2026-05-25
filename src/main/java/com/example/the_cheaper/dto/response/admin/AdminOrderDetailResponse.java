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
    private Long accountId;
    private BigDecimal subTotal;
    private BigDecimal finalTotal;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private List<UserOrderItemResponse> items;
}



