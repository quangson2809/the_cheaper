package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminCreatePaymentMethodRequest;
import com.example.the_cheaper.dto.request.admin.AdminUpdatePaymentMethodRequest;
import com.example.the_cheaper.dto.response.common.PaymentMethodResponse;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.service.admin.AdminPaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.security.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payment-methods")
@RequiredArgsConstructor
public class AdminPaymentMethodController {

    private final AdminPaymentMethodService adminPaymentMethodService;

    // GET /admin/payment-methods — Lấy tất cả (bao gồm inactive)
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentMethodResponse>>> getAllPaymentMethods(
            @CurrentUser AccountEntity currentUser) {
        try {
            List<PaymentMethodResponse> response = adminPaymentMethodService.getAllPaymentMethods(currentUser);
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Lấy danh sách phương thức thanh toán thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/admin/payment-methods"));
        }
    }

    // GET /admin/payment-methods/{id} — Lấy chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> getPaymentMethod(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        try {
            PaymentMethodResponse response = adminPaymentMethodService.getPaymentMethod(id, currentUser);
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Lấy chi tiết phương thức thanh toán thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/admin/payment-methods/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/admin/payment-methods/" + id));
        }
    }

    // POST /admin/payment-methods — Tạo mới
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> createPaymentMethod(
            @Valid @RequestBody AdminCreatePaymentMethodRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            PaymentMethodResponse response = adminPaymentMethodService.createPaymentMethod(request, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Tạo phương thức thanh toán thành công"));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT.value(),
                            e.getMessage(), "/api/admin/payment-methods"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/admin/payment-methods"));
        }
    }

    // PUT /admin/payment-methods/{id} — Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> updatePaymentMethod(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdatePaymentMethodRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            PaymentMethodResponse response = adminPaymentMethodService.updatePaymentMethod(id, request, currentUser);
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Cập nhật phương thức thanh toán thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/admin/payment-methods/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/admin/payment-methods/" + id));
        }
    }

    // DELETE /admin/payment-methods/{id} — Xóa mềm
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePaymentMethod(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        try {
            adminPaymentMethodService.deletePaymentMethod(id, currentUser);
            return ResponseEntity.ok(
                    ApiResponse.success(null, "Vô hiệu hóa phương thức thanh toán thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/admin/payment-methods/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/admin/payment-methods/" + id));
        }
    }
}
