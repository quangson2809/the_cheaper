package com.example.the_cheaper.dto.response.admin;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVariantResponse {
    private Long id;
    private String sku;
    private int stock;
    private int countSold;
    private BigDecimal overiteSalePrice;
    private List<AdminOptionValueResponse> optionValues;
}


