package com.example.the_cheaper.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductOverviewResponse {
    private Long id;
    private String name;
    private String brandName;
    private String categoryName;
    private BigDecimal salePrice;
    private BigDecimal comparePrice;
    private int status;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private int totalStock;
    private int totalSold;
}

