package com.example.the_cheaper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_account_created_at", columnList = "account_id, created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity {
    private static final Set<OrderStatus> PENDING_TRANSITIONS =
            EnumSet.of(OrderStatus.PROCESSING, OrderStatus.CANCELED);
    private static final Set<OrderStatus> PROCESSING_TRANSITIONS =
            EnumSet.of(OrderStatus.SHIPPING, OrderStatus.CANCELED);
    private static final Set<OrderStatus> SHIPPING_TRANSITIONS =
            EnumSet.of(OrderStatus.DELIVERED);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "final_amount", precision = 19, scale = 2)
    private BigDecimal finalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();

    private String phone;
    private String receiver;
    private String location;

    // Snapshot: lưu code của phương thức thanh toán tại thời điểm đặt hàng
    // Không dùng @ManyToOne để tránh phụ thuộc vào PaymentMethodEntity
    @Column(name = "payment_method_code", length = 50)
    private String paymentMethodCode;

    @Column(name = "payment_status")
    private int paymentStatus;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public int getCountItems() {
        return items.size();
    }

    public boolean isPaid() {
        return paymentStatus == 1;
    }

    public boolean canTransitionTo(OrderStatus targetStatus) {
        if (status == null || targetStatus == null || status == targetStatus) {
            return false;
        }

        return switch (status) {
            case PENDING -> PENDING_TRANSITIONS.contains(targetStatus);
            case PROCESSING -> PROCESSING_TRANSITIONS.contains(targetStatus);
            case SHIPPING -> SHIPPING_TRANSITIONS.contains(targetStatus);
            case DELIVERED, CANCELED, REFUNDED -> false;
        };
    }

    public void transitionTo(OrderStatus targetStatus) {
        if (!canTransitionTo(targetStatus)) {
            throw new IllegalStateException(
                    "Không thể chuyển trạng thái từ " + status + " sang " + targetStatus);
        }
        this.status = targetStatus;
    }
}
