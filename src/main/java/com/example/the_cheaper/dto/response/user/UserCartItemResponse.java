package com.example.the_cheaper.dto.response.user;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCartItemResponse {
    private Long id;
    private Long productId;
    private int quantity;
    private BigDecimal price;
    private String productName;
    private String thumbnail;
    private List<String> optionNames;
}


