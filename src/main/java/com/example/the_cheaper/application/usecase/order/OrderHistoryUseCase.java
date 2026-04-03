package com.example.the_cheaper.application.usecase.order;

import com.example.the_cheaper.domain.model.Order;
import com.example.the_cheaper.domain.repository.OrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderHistoryUseCase {

    private final OrderRepository orderRepository;

    public OrderHistoryUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getOrderHistory(Long userId, int page, int limit) {
        return orderRepository.findAll(); // Simple for now
    }

    public Order getOrderDetail(Long userId, Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

}


