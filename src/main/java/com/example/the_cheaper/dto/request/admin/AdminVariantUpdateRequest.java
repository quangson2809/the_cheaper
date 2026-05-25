package com.example.the_cheaper.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVariantUpdateRequest {
    @NotNull
    private Long id;

    private BigDecimal overrideSalePrice;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private List<Long> optionValueAdds;

    private List<Long> optionValueSubs;
}
