package com.example.the_cheaper.interfaces.rest.dto.response.user;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private String productName;
    private Long productId;
    private Long variantId;
    private String optionValue;
    private int quantity;
    private BigDecimal unitPrice;
}
