package com.example.the_cheaper.dto.request.admin;

import com.example.the_cheaper.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderFilterRequest {
    private OrderStatus status;
    @NotNull(message = "Page không được null")
    private int page;
    @NotNull(message = "limit không được null")
    private int limit;
}

