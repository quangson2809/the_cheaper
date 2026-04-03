package com.example.the_cheaper.domain.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    private Long id;
    private String sku;
    private int stock;
    private Money overiteSalePrice;
    private List<OptionValue> optionValues;

    public void decreaseStock(int quantity) {
        if (stock < quantity) throw new IllegalStateException("Not enough stock");
        stock -= quantity;
        if (stock < 0) {
            stock = 0;
        }
    }

    public void increaseStock(int quantity) {
        stock += quantity;
    }

    public boolean isInStock() {
        return stock > 0;
    }
}
