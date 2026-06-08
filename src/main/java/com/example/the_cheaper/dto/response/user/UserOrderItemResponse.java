package com.example.the_cheaper.dto.response.user;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderItemResponse {
    private String productName;
    private Long productId;
    private String optionValue;
    private int quantity;
    private BigDecimal price;
    private String thumbnailUrl;
    private BigDecimal unitPrice;
}

