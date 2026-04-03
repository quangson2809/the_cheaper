package com.example.the_cheaper.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Long accountId;
    private Money subTotal;
    private Money finalTotal;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private List<OrderItem> items;

    public void calculateTotals() {
        Money newSubTotal = items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(Money.ZERO, Money::add);
        this.subTotal = newSubTotal;
        // Apply discounts, points, etc.
        this.finalTotal = newSubTotal;
    }

    public void cancelOrder() {
        if (status == OrderStatus.PENDING || status == OrderStatus.PAID) {
            this.status = OrderStatus.CANCELED;
            // Emit event for stock restoration
        }
    }
}
