package com.example.the_cheaper.dto.response.user;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.example.the_cheaper.dto.response.admin.AdminOptionValueResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVariantInfoResponse {
    private Long id;
    private String sku;
    private BigDecimal price; // Nếu variant có giá riêng thì lấy, không thì lấy giá chung của product
    private Integer stock;
    private boolean inStock;

    private Map<String, String> attributes;
}


