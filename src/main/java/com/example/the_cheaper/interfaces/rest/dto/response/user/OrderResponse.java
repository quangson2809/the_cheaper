package com.example.the_cheaper.interfaces.rest.dto.response.user;

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
    private OrderStatus status;
    private BigDecimal subTotal;
    private BigDecimal finalTotal;
    private LocalDateTime createdAt;
    private String receiver;
    private String location;
    private List<OrderItemResponse> items;
}
