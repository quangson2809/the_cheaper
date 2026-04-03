package com.example.the_cheaper.application.usecase.admin;

import com.example.the_cheaper.application.command.UpdateOrderStatusCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.model.Order;
import com.example.the_cheaper.domain.repository.OrderRepository;
import com.example.the_cheaper.domain.service.OrderDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;

    public ManageOrderUseCase(OrderRepository orderRepository, OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
    }

    public Order updateOrderStatus(UpdateOrderStatusCommand command) {
        throw new NotImplementedException("Chức năng cập nhật trạng thái đơn hàng chưa được triển khai");
    }

    public Order getOrderDetail(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<Order> listOrders(int page, int limit, String status) {
        return orderRepository.findAll();
    }
}
