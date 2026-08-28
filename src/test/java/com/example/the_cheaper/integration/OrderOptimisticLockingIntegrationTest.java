package com.example.the_cheaper.integration;

import com.example.the_cheaper.TheCheaperApplication;
import com.example.the_cheaper.entity.OrderEntity;
import com.example.the_cheaper.entity.OrderStatus;
import com.example.the_cheaper.entity.ProductVariantEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.repository.OrderRepository;
import com.example.the_cheaper.repository.ProductVariantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest(classes = TheCheaperApplication.class)
class OrderOptimisticLockingIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "false");
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
