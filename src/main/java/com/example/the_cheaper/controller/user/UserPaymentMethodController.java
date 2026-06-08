package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.response.common.PaymentMethodResponse;
import com.example.the_cheaper.repository.PaymentMethodRepository;
import com.example.the_cheaper.mapper.admin.AdminPaymentMethodMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class UserPaymentMethodController {

    private final PaymentMethodRepository paymentMethodRepository;
    private final AdminPaymentMethodMapper adminPaymentMethodMapper;

    // GET /payment-methods — Chỉ trả về các phương thức đang active
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentMethodResponse>>> getPaymentMethods() {
        try {
            List<PaymentMethodResponse> response = paymentMethodRepository.findAll()
                    .stream()
                    .map(adminPaymentMethodMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Lấy danh sách phương thức thanh toán thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/payment-methods"));
        }
    }
}
