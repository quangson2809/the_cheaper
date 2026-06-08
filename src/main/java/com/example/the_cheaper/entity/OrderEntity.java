package com.example.the_cheaper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public int getCountItems(){
        return items.size();
    }

    public String getPaymentStatus() {
        return paymentStatus ==1 ? "Đã thanh toán" : "Chưa thanh toán";
    }

    public String getStatusLabel(){
        switch (status) {
            case PENDING:
                return "Đang chờ xử lý";
            case PROCESSING:
                return "Đang xử lý";
            case SHIPPING:
                return "Đang giao hàng";
            case DELIVERED:
                return "Đã giao hàng";
            case CANCELED:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }
}
