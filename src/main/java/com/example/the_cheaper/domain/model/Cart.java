package com.example.the_cheaper.domain.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Long id;
    private Long accountId;
    private List<CartItem> items;

    public void addItem(Long variantId, int quantity) {
        CartItem exitingItem = this.items.stream()
                .filter(item -> item.getVariantId().equals(variantId))
                .findFirst()
                .orElse(null);
        if (exitingItem == null)
            this.items.add(new CartItem(null, variantId, quantity));
        else
            exitingItem.setQuantity(exitingItem.getQuantity() + quantity);
    }

    public void removeItem(Long variantId) {
        items.removeIf(item -> item.getVariantId().equals(variantId));
    }

    public void clearCart() {
        items.clear();
    }
}

