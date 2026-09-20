package com.example.the_cheaper.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.payment.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
