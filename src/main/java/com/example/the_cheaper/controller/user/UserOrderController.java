package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.user.UserCreateOrderRequest;
import com.example.the_cheaper.dto.response.user.UserOrderResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;

    // ─── POST /orders — Mua hàng (Thanh toán) ──────────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<UserOrderResponse>> createOrder(
            @CurrentUser AccountEntity currentUser,
            @RequestBody @Valid UserCreateOrderRequest request) {
        try {
            UserOrderResponse response = orderService.createOrder(currentUser.getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Đặt hàng thành công"));
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

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserOrderResponse>>> getMyOrders(
            @CurrentUser AccountEntity currentUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Page<UserOrderResponse> response = orderService.getMyOrders(currentUser.getId(), page, limit);
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
    public ResponseEntity<ApiResponse<UserOrderResponse>> getOrderDetail(
            @CurrentUser AccountEntity currentUser,
            @PathVariable("order_id") Long orderId) {
        try {
            String role = currentUser.getRole() != null ? currentUser.getRole().getName() : null;
            UserOrderResponse response = orderService.getOrderDetail(orderId, currentUser.getId(), role);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết đơn hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/orders/" + orderId));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(),
                            "/api/orders/" + orderId));
        }
    }

    @PostMapping("/{order_id}/cancel")
    public ResponseEntity<ApiResponse<UserOrderResponse>> cancelOrder(
            @CurrentUser AccountEntity currentUser,
            @PathVariable("order_id") Long orderId) {
        try {
            UserOrderResponse response = orderService.cancelOrder(orderId, currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success(response, "Hủy đơn hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/orders/" + orderId + "/cancel"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/orders/" + orderId + "/cancel"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/orders/" + orderId + "/cancel"));
        }
    }
}
