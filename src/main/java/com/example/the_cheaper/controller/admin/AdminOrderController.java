package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminOrderFilterRequest;
import com.example.the_cheaper.dto.request.admin.AdminOrderStatusUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminOrderDetailResponse;
import com.example.the_cheaper.dto.response.admin.AdminOrderOverviewResponse;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.service.admin.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    @PreAuthorize("hasAuthority('ORDER_READ')")
    public ResponseEntity<ApiResponse<Page<AdminOrderOverviewResponse>>> getListOrders(
            AdminOrderFilterRequest request) {
        Page<AdminOrderOverviewResponse> response = adminOrderService.getListOrders(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách đơn hàng thành công"));
    }

    @GetMapping("/{order_id}")
    @PreAuthorize("hasAuthority('ORDER_READ')")
    public ResponseEntity<ApiResponse<AdminOrderDetailResponse>> getOrderDetail(
            @PathVariable("order_id") Long orderId) {
        try {
            AdminOrderDetailResponse response = adminOrderService.getOrderDetail(orderId);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết đơn hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/orders/" + orderId));
        }
    }

    @PatchMapping("/{order_id}/status")
    @PreAuthorize("hasAuthority('ORDER_UPDATE')")
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
        } catch (InvalidInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                            "/api/admin/orders/" + orderId + "/status"));
        }
    }
}
