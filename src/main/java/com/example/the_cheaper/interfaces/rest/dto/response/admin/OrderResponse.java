package com.example.the_cheaper.interfaces.rest.dto.response.admin;

import com.example.the_cheaper.interfaces.rest.dto.response.user.OrderItemResponse;
import com.example.the_cheaper.domain.model.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long accountId;
    private BigDecimal subTotal;
    private BigDecimal finalTotal;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private List<OrderItemResponse> items;
}
