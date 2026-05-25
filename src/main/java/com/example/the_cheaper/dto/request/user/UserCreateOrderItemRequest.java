package com.example.the_cheaper.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateOrderItemRequest {
    private Long variantId;
    private int quantity;
    private BigDecimal price;
}
