package com.example.the_cheaper.interfaces.rest.controller.admin;

import com.example.the_cheaper.application.usecase.admin.ManageOrderUseCase;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.exception.ResourceNotFoundException;
import com.example.the_cheaper.interfaces.rest.dto.ApiResponse;
import com.example.the_cheaper.interfaces.rest.dto.request.admin.OrderStatusUpdateRequest;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.OrderResponse;
import com.example.the_cheaper.interfaces.rest.mapper.admin.AdminOrderMapper;
import com.example.the_cheaper.interfaces.rest.mapper.user.UserOrderMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final ManageOrderUseCase manageOrderUseCase;
    private final AdminOrderMapper adminOrderMapper;
    private final UserOrderMapper userOrderMapper;

    public AdminOrderController(ManageOrderUseCase manageOrderUseCase, AdminOrderMapper adminOrderMapper,
            UserOrderMapper userOrderMapper) {
        this.manageOrderUseCase = manageOrderUseCase;
        this.adminOrderMapper = adminOrderMapper;
        this.userOrderMapper = userOrderMapper;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<OrderResponse>>> listOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status) {
        try {
            List<OrderResponse> response = manageOrderUseCase.listOrders(page, limit, status).stream()
                    .map(adminOrderMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách đơn hàng thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/orders"));
        }
    }

    @GetMapping("/{order_id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(
            @PathVariable("order_id") Long orderId) {
        try {
            OrderResponse response = adminOrderMapper.toResponse(manageOrderUseCase.getOrderDetail(orderId));
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết đơn hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/orders/" + orderId));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(),
                            "/api/admin/orders/" + orderId));
        }
    }

    @PatchMapping("/{order_id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable("order_id") Long orderId,
            @RequestBody OrderStatusUpdateRequest request) {
        try {
            OrderResponse response = adminOrderMapper
                    .toResponse(manageOrderUseCase.updateOrderStatus(userOrderMapper.toCommand(orderId, request)));
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật trạng thái đơn hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/orders/" + orderId + "/status"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(),
                            "/api/admin/orders/" + orderId + "/status"));
        }
    }

}
