package com.example.the_cheaper.dto.response.admin;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductSales {
    private Long productId;
    private String productName;
    private long totalSales;
    private BigDecimal totalRevenue;
}

