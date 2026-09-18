package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.entity.OrderEntity;
import com.example.the_cheaper.entity.OrderStatus;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.user.UserOrderMapper;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.CartRepository;
import com.example.the_cheaper.repository.OrderRepository;
import com.example.the_cheaper.repository.PaymentMethodRepository;
import com.example.the_cheaper.repository.ProductVariantRepository;
import com.example.the_cheaper.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceAuthorizationTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserOrderMapper orderMapper;
    @Mock
    private ProductVariantRepository productVariantRepository;
    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository,
                cartRepository,
                orderMapper,
                productVariantRepository,
                paymentMethodRepository,
                accountRepository
        );
    }

    @Test
    void getOrderDetailUsesOwnerScopedQuery() {
        long accountId = 10L;
        long orderId = 100L;
        OrderEntity order = OrderEntity.builder().id(orderId).build();

        when(orderRepository.findByIdAndAccountId(orderId, accountId)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(null);

        orderService.getOrderDetail(orderId, accountId);

        verify(orderRepository).findByIdAndAccountId(orderId, accountId);
    }

    @Test
    void getOrderDetailDoesNotFallbackToUnscopedLookup() {
        long accountId = 10L;
        long orderId = 200L;

        when(orderRepository.findByIdAndAccountId(orderId, accountId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderDetail(orderId, accountId));

        verify(orderRepository).findByIdAndAccountId(orderId, accountId);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void cancelOrderUsesOwnerScopedQuery() {
        long accountId = 10L;
        long orderId = 300L;
        OrderEntity order = OrderEntity.builder()
                .id(orderId)
                .paymentMethodCode("COD")
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findByIdAndAccountId(orderId, accountId)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(null);

        orderService.cancelOrder(orderId, accountId);

        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository).findByIdAndAccountId(orderId, accountId);
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrderRejectsTerminalOrder() {
        long accountId = 10L;
        long orderId = 400L;
        OrderEntity order = OrderEntity.builder()
                .id(orderId)
                .paymentMethodCode("COD")
                .status(OrderStatus.DELIVERED)
                .build();

        when(orderRepository.findByIdAndAccountId(orderId, accountId)).thenReturn(Optional.of(order));

        assertThrows(InvalidInputException.class, () -> orderService.cancelOrder(orderId, accountId));
        verify(orderRepository).findByIdAndAccountId(orderId, accountId);
    }
}
