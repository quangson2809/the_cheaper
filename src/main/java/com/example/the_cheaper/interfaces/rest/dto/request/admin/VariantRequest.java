package com.example.the_cheaper.interfaces.rest.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantRequest {
    @NotBlank(message = "SKU is required")
    private String sku;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    private BigDecimal overiteSalePrice;
    private List<Long> optionValueIds;
    private Long productId;
}
