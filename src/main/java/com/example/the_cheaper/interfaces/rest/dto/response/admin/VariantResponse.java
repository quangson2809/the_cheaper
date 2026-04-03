package com.example.the_cheaper.interfaces.rest.dto.response.admin;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantResponse {
    private Long id;
    private String sku;
    private int stock;
    private BigDecimal overiteSalePrice;
    private List<OptionValueResponse> optionValues;
}
