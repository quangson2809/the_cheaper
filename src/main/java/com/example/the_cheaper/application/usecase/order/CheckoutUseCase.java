package com.example.the_cheaper.application.usecase.order;

import com.example.the_cheaper.application.command.PlaceOrderCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.model.Order;
import com.example.the_cheaper.domain.repository.CartRepository;
import com.example.the_cheaper.domain.repository.OrderRepository;
import com.example.the_cheaper.domain.repository.ProductRepository;
import com.example.the_cheaper.domain.service.OrderDomainService;
import org.springframework.stereotype.Service;

@Service
public class CheckoutUseCase {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final ProductRepository productRepository;

    public CheckoutUseCase(CartRepository cartRepository, OrderRepository orderRepository,
            OrderDomainService orderDomainService, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.productRepository = productRepository;
    }

    public Order placeOrder(PlaceOrderCommand command) {
        throw new NotImplementedException("Chức năng đặt hàng chưa được triển khai");
    }

}
