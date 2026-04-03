package com.example.the_cheaper.infrastructure.persistence.adapter;

import com.example.the_cheaper.domain.model.Order;
import com.example.the_cheaper.domain.repository.OrderRepository;
import com.example.the_cheaper.infrastructure.persistence.entity.OrderEntity;
import com.example.the_cheaper.infrastructure.persistence.mapper.OrderPersistenceMapper;
import com.example.the_cheaper.infrastructure.persistence.repository.JpaOrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
    private final OrderPersistenceMapper orderPersistenceMapper;

    public OrderRepositoryImpl(
            JpaOrderRepository jpaOrderRepository,
            OrderPersistenceMapper orderPersistenceMapper
    ) {
        this.jpaOrderRepository = jpaOrderRepository;
        this.orderPersistenceMapper = orderPersistenceMapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = orderPersistenceMapper.toEntity(order);
        OrderEntity savedEntity = jpaOrderRepository.save(orderEntity);
        return orderPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaOrderRepository.findById(id)
                .map(orderPersistenceMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpaOrderRepository.findAll().stream()
                .map(orderPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaOrderRepository.deleteById(id);
    }

    @Override
    public void delete(Order order) {
        jpaOrderRepository.delete(orderPersistenceMapper.toEntity(order));
    }

    @Override
    public List<Order> findByAccountId(Long accountId) {
        return jpaOrderRepository.findByAccountId(accountId).stream()
                .map(orderPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

}
