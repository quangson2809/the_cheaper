package com.example.the_cheaper.dto.request.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProductFilterRequest {
    private Long brandId, categoryId;
    int page =1,limit =10;
    BigDecimal minPrice, maxPrice;
    String sortBy ="price";
}


