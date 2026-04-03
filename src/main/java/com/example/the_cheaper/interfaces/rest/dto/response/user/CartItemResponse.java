package com.example.the_cheaper.interfaces.rest.dto.response.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long variantId;
    private int quantity;
    private VariantInfoResponse variantInfo;
}
