package com.example.the_cheaper.cart.dto.response.user;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCartResponse {
    private List<UserCartItemResponse> items;
    private BigDecimal totalPrice;
}


