package com.example.the_cheaper.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long variantId;
    private int quantity;
    private Money unitPrice;

    public Money getSubTotal() {
        return unitPrice != null ? unitPrice.multiply(quantity) : Money.ZERO;
    }
}

