package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.entity.OrderEntity;
import com.example.the_cheaper.mapper.admin.AdminOrderMapper;
import com.example.the_cheaper.repository.OrderRepository;
import com.example.the_cheaper.service.admin.AdminOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceAuthorizationTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AdminOrderMapper orderMapper;

    private AdminOrderService adminOrderService;

    @BeforeEach
    void setUp() {
        adminOrderService = new AdminOrderService(orderRepository, orderMapper);
    }

    @Test
    void getOrderDetailUsesGlobalOrderLookupWithoutOwnerConstraint() {
        long orderId = 100L;
        OrderEntity order = OrderEntity.builder().id(orderId).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDetailResponse(order)).thenReturn(null);

        adminOrderService.getOrderDetail(orderId);

        verify(orderRepository).findById(orderId);
    }
}
