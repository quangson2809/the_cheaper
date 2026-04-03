package com.example.the_cheaper.interfaces.rest.controller.user;

import com.example.the_cheaper.domain.exception.InvalidInputException;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.exception.ResourceNotFoundException;
import com.example.the_cheaper.interfaces.rest.dto.ApiResponse;
import com.example.the_cheaper.interfaces.rest.dto.request.user.CheckoutRequest;
import com.example.the_cheaper.interfaces.rest.dto.response.user.OrderResponse;
import com.example.the_cheaper.application.usecase.order.CheckoutUseCase;
import com.example.the_cheaper.application.usecase.order.OrderHistoryUseCase;
import com.example.the_cheaper.interfaces.rest.mapper.user.UserOrderMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CheckoutUseCase checkoutUseCase;
    private final OrderHistoryUseCase orderHistoryUseCase;
    private final UserOrderMapper orderMapper;

    public OrderController(CheckoutUseCase checkoutUseCase, OrderHistoryUseCase orderHistoryUseCase,
            UserOrderMapper orderMapper) {
        this.checkoutUseCase = checkoutUseCase;
        this.orderHistoryUseCase = orderHistoryUseCase;
        this.orderMapper = orderMapper;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestParam Long userId,
            @RequestBody CheckoutRequest request) {
        try {
            OrderResponse response = orderMapper.toResponse(checkoutUseCase.placeOrder(orderMapper.toCommand(userId, request)));
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Đặt hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/orders"));
        } catch (InvalidInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/orders"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/orders"));
        }
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(@RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<OrderResponse> response = orderHistoryUseCase.getOrderHistory(userId, page, limit).stream()
                    .map(orderMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy lịch sử đơn hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/orders"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/orders"));
        }
    }

    @GetMapping("/{order_id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(@RequestParam Long userId,
            @PathVariable("order_id") Long orderId) {
        try {
            OrderResponse response = orderMapper.toResponse(orderHistoryUseCase.getOrderDetail(userId, orderId));
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết đơn hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/orders/" + orderId));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/orders/" + orderId));
        }
    }

}
