package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductCreateRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;
    private Long materialId;

    @NotNull(message = "Sale price is required")
    private BigDecimal salePrice;
    @NotNull(message = "Compare price is required")
    private BigDecimal comparePrice;
    private Long brandId;
    private Long categoryId;
    private List<AdminVariantCreateRequest> variants;
}


