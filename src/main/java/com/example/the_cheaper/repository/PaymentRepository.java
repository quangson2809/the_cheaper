package com.example.the_cheaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
