package com.example.the_cheaper.interfaces.rest.dto.response.admin;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String material;
    private BigDecimal salePrice;
    private BigDecimal comparePrice;
    private BrandResponse brand;
    private String brandName;
    private CategoryResponse category;
    private String categoryName;
    private List<VariantResponse> variants;
    private List<ProductImageResponse> images;
}
