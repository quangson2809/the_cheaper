package com.example.the_cheaper.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderItemResponse {
    private Long productId;
    private int quantity;
    private BigDecimal price;
    private String productName;
    private String thumbnail;
    private String optionNames;
}
