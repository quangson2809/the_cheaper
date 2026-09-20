package com.example.the_cheaper.order.dto.request.admin;

import com.example.the_cheaper.order.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderStatusUpdateRequest {
    @NotNull(message = "Order status is required")
    private OrderStatus status;
}

