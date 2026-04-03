package com.example.the_cheaper.domain.repository;

import com.example.the_cheaper.domain.model.Order;

import java.util.List;

public interface OrderRepository extends BaseRepository<Order, Long> {
    List<Order> findByAccountId(Long accountId);
}
