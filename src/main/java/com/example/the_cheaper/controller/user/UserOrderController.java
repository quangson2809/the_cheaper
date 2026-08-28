package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.user.UserCreateOrderRequest;
import com.example.the_cheaper.dto.response.user.UserOrderResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER_ORDER_CREATE')")
    public ResponseEntity<ApiResponse<UserOrderResponse>> createOrder(
            @CurrentUser AccountEntity currentUser,
            @RequestBody @Valid UserCreateOrderRequest request) {
        UserOrderResponse response = orderService.createOrder(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Đặt hàng thành công"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_ORDER_READ')")
    public ResponseEntity<ApiResponse<Page<UserOrderResponse>>> getMyOrders(
            @CurrentUser AccountEntity currentUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<UserOrderResponse> response = orderService.getMyOrders(currentUser.getId(), page, limit);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy lịch sử đơn hàng thành công"));
    }

    @GetMapping("/{order_id}")
    @PreAuthorize("hasAuthority('USER_ORDER_READ')")
    public ResponseEntity<ApiResponse<UserOrderResponse>> getOrderDetail(
            @CurrentUser AccountEntity currentUser,
            @PathVariable("order_id") Long orderId) {
        UserOrderResponse response = orderService.getOrderDetail(orderId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết đơn hàng thành công"));
    }

    @PostMapping("/{order_id}/cancel")
    @PreAuthorize("hasAuthority('USER_ORDER_CANCEL')")
    public ResponseEntity<ApiResponse<UserOrderResponse>> cancelOrder(
            @CurrentUser AccountEntity currentUser,
            @PathVariable("order_id") Long orderId) {
        UserOrderResponse response = orderService.cancelOrder(orderId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Hủy đơn hàng thành công"));
    }
}
