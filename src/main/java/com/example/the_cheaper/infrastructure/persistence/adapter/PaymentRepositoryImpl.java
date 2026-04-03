package com.example.the_cheaper.infrastructure.persistence.adapter;

import com.example.the_cheaper.domain.model.Payment;
import com.example.the_cheaper.domain.repository.PaymentRepository;
import com.example.the_cheaper.infrastructure.persistence.entity.PaymentEntity;
import com.example.the_cheaper.infrastructure.persistence.mapper.PaymentPersistenceMapper;
import com.example.the_cheaper.infrastructure.persistence.repository.JpaPaymentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;
    private final PaymentPersistenceMapper paymentPersistenceMapper;

    public PaymentRepositoryImpl(
            JpaPaymentRepository jpaPaymentRepository,
            PaymentPersistenceMapper paymentPersistenceMapper
    ) {
        this.jpaPaymentRepository = jpaPaymentRepository;
        this.paymentPersistenceMapper = paymentPersistenceMapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity paymentEntity = paymentPersistenceMapper.toEntity(payment);
        PaymentEntity savedEntity = jpaPaymentRepository.save(paymentEntity);
        return paymentPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return jpaPaymentRepository.findById(id)
                .map(paymentPersistenceMapper::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return jpaPaymentRepository.findAll().stream()
                .map(paymentPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaPaymentRepository.deleteById(id);
    }

    @Override
    public void delete(Payment payment) {
        jpaPaymentRepository.delete(paymentPersistenceMapper.toEntity(payment));
    }

}
