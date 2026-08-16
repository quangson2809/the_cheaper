package com.example.the_cheaper.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.the_cheaper.entity.MaterialEntity;
import com.example.the_cheaper.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.entity.OrderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByAccountId(Long accountId);

    Page<OrderEntity> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    @Query("SELECT o FROM OrderEntity o WHERE (:status IS NULL OR o.status = :status) ")
    Page<OrderEntity> findByAdminFilter(
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    @Query("SELECT o FROM OrderEntity o WHERE " +
            " o.id = :id ")
    Page<OrderEntity> findOrderByIdContainingIgnoreCase(
            @Param("id") Long id,
            Pageable pageable);

    @Query("SELECT COUNT(o) > 0 FROM OrderEntity o " +
           "JOIN o.items oi " +
           "WHERE o.account.id = :accountId " +
           "AND oi.variant.product.id = :productId " +
           "AND o.status = com.example.the_cheaper.entity.OrderStatus.DELIVERED " +
           "AND o.paymentStatus = 1")
    boolean existsDeliveredPaidOrderByAccountAndProduct(
            @Param("accountId") Long accountId,
            @Param("productId") Long productId
    );

    @Query("SELECT MONTH(o.createdAt), SUM(o.finalAmount) FROM OrderEntity o WHERE YEAR(o.createdAt) = :year AND o.paymentStatus = 1 GROUP BY MONTH(o.createdAt)")
    List<Object[]> getMonthlyRevenue(@Param("year") int year);

    @Query("SELECT MONTH(o.createdAt), SUM(oi.quantity) FROM OrderEntity o JOIN o.items oi WHERE YEAR(o.createdAt) = :year AND o.paymentStatus = 1 GROUP BY MONTH(o.createdAt)")
    List<Object[]> getMonthlySoldQuantity(@Param("year") int year);

    @Query("SELECT o.status, COUNT(o) FROM OrderEntity o GROUP BY o.status")
    List<Object[]> getOrderStatusCounts();

    @Query("select count(o) from OrderEntity o where o.paymentStatus = 1 and o.createdAt between :from and :to")
    int countByPayAtBetween(@Param("from")LocalDateTime from,
                                @Param("to")LocalDateTime to);
    @Query("select coalesce(sum(o.finalAmount)) from OrderEntity o where o.paymentStatus = 1 and o.createdAt between :from and :to")
    BigDecimal revenueByPayAtBetween(@Param("from")LocalDateTime from,
                                @Param("to")LocalDateTime to);
}
