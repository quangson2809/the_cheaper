package com.example.the_cheaper.integration;

import com.example.the_cheaper.entity.OrderEntity;
import com.example.the_cheaper.entity.OrderStatus;
import com.example.the_cheaper.entity.ProductVariantEntity;
import com.example.the_cheaper.repository.OrderRepository;
import com.example.the_cheaper.repository.ProductVariantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderOptimisticLockingIntegrationTest extends com.example.the_cheaper.testconfig.MySqlIntegrationTest {

    private Long createdOrderId;
    private Long createdVariantId;

    @AfterEach
    void cleanUpCommittedRows() {
        if (createdOrderId != null) orderRepository.deleteById(createdOrderId);
        if (createdVariantId != null) productVariantRepository.deleteById(createdVariantId);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Test
    void staleOrderUpdateIsRejectedByOptimisticLocking() {
        OrderEntity created = orderRepository.saveAndFlush(
                OrderEntity.builder()
                        .status(OrderStatus.PENDING)
                        .paymentStatus(0)
                        .paymentMethodCode("COD")
                        .build());
        createdOrderId = created.getId();
        assertNotNull(created.getVersion());

        OrderEntity stale = orderRepository.findById(created.getId()).orElseThrow();
        OrderEntity winner = orderRepository.findById(created.getId()).orElseThrow();

        winner.transitionTo(OrderStatus.PROCESSING);
        orderRepository.saveAndFlush(winner);

        stale.transitionTo(OrderStatus.CANCELED);

        assertThrows(OptimisticLockingFailureException.class,
                () -> orderRepository.saveAndFlush(stale));
    }

    @Test
    void staleProductVariantUpdateIsRejectedByOptimisticLocking() {
        ProductVariantEntity created = productVariantRepository.saveAndFlush(
                ProductVariantEntity.builder()
                        .sku("LOCK-TEST-SKU")
                        .stock(10)
                        .sold(0)
                        .build());
        createdVariantId = created.getId();
        assertNotNull(created.getVersion());

        ProductVariantEntity stale = productVariantRepository.findById(created.getId()).orElseThrow();
        ProductVariantEntity winner = productVariantRepository.findById(created.getId()).orElseThrow();

        winner.setStock(9);
        productVariantRepository.saveAndFlush(winner);

        stale.setStock(8);

        assertThrows(OptimisticLockingFailureException.class,
                () -> productVariantRepository.saveAndFlush(stale));
    }
}
