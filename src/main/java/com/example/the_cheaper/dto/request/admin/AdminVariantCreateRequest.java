package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVariantCreateRequest {
    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    private BigDecimal overrideSalePrice;

    @NotNull(message = "Option is required")
    private List<Long> optionValueIds;
}

