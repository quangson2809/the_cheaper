package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminOrderFilterRequest;
import com.example.the_cheaper.dto.request.admin.AdminOrderStatusUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminOrderDetailResponse;
import com.example.the_cheaper.dto.response.admin.AdminOrderOverviewResponse;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.service.admin.AdminOrderService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.security.CurrentUser;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminOrderOverviewResponse>>> listOrders(
            @CurrentUser AccountEntity currentUser,
            AdminOrderFilterRequest request) {
        try {
            Page<AdminOrderOverviewResponse> response = adminOrderService.listOrders(currentUser, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách đơn hàng thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/orders"));
        }
    }

    @GetMapping("/{order_id}")
    public ResponseEntity<ApiResponse<AdminOrderDetailResponse>> getOrderDetail(
            @PathVariable("order_id") Long orderId,
            @CurrentUser AccountEntity currentUser
    ) {
        try {
            AdminOrderDetailResponse response = adminOrderService.getOrderDetail(currentUser,orderId);
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
    public ResponseEntity<ApiResponse<AdminOrderOverviewResponse>> updateOrderStatus(
            @PathVariable("order_id") Long orderId,
            @RequestBody AdminOrderStatusUpdateRequest request) {
        try {
            AdminOrderOverviewResponse response = adminOrderService.updateOrderStatus(orderId, request);
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


