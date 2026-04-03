package com.example.the_cheaper.interfaces.rest.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;
    private String material;

    @NotNull(message = "Sale price is required")
    private BigDecimal salePrice;

    private BigDecimal comparePrice;
    private Long brandId;
    private Long categoryId;
    private List<VariantRequest> variants;
    private List<ProductImageRequest> images;
    private List<OptionAttributeRequest> optionAttributes;
}
