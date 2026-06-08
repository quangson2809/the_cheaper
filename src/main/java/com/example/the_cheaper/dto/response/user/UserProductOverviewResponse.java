package com.example.the_cheaper.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProductOverviewResponse {
    private Long id;
    private String name;
    private String brand;
    private String category;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private String thumbnailUrl;
}

