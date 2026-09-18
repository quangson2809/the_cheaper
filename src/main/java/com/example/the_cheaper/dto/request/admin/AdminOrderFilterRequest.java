package com.example.the_cheaper.dto.request.admin;

import com.example.the_cheaper.entity.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
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
    @Min(1)
    @Builder.Default
    private int page = 1;
    @Min(1)
    @Max(100)
    @Builder.Default
    private int limit = 10;
}

