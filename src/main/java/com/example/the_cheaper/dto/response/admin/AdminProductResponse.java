package com.example.the_cheaper.dto.response.admin;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductResponse {
    private Long id;
    private String name;
    private String description;
    private AdminBrandResponse brand;
    private AdminCategoryResponse category;
    private AdminMaterialResponse material;
    private BigDecimal salePrice;
    private BigDecimal comparePrice;
    private int status;
    private List<AdminProductImageResponse> images;
    private List<AdminVariantResponse> variants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


