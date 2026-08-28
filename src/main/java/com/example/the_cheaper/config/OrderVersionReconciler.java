package com.example.the_cheaper.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(90)
public class OrderVersionReconciler implements CommandLineRunner {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        entityManager.createNativeQuery(
                        "UPDATE orders SET version = 0 WHERE version IS NULL")
                .executeUpdate();

        entityManager.createNativeQuery(
                        "UPDATE product_variants SET version = 0 WHERE version IS NULL")
                .executeUpdate();
    }
}
