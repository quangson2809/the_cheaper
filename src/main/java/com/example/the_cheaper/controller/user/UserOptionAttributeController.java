package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.response.user.UserOptionAttributeResponse;
import com.example.the_cheaper.dto.response.user.UserOptionValueResponse;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.service.product.OptionAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User API: đọc thông tin thuộc tính sản phẩm (Size, Màu sắc…) và các giá trị của chúng.
 * Dùng để hỗ trợ tính năng lọc/tìm kiếm sản phẩm phía client.
 *
 * Các endpoint này là PUBLIC (không yêu cầu đăng nhập).
 *
 * Base URL: /api/attributes
 */
@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
public class UserOptionAttributeController {

    private final OptionAttributeService optionAttributeService;

    /**
     * GET /api/attributes
     * Lấy danh sách tất cả thuộc tính (kèm danh sách value của mỗi thuộc tính).
     * Dùng cho sidebar lọc sản phẩm.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserOptionAttributeResponse>>> listOptionAttributes() {
        try {
            List<UserOptionAttributeResponse> response =
                    optionAttributeService.listOptionAttributes();
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Lấy danh sách thuộc tính thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/attributes"));
        }
    }

    /**
     * GET /api/attributes/{attributeId}/values
     * Lấy danh sách values của một thuộc tính cụ thể theo attributeId.
     * Ví dụ: GET /api/attributes/1/values → [S, M, L, XL]
     */
    @GetMapping("/{attributeId}/values")
    public ResponseEntity<ApiResponse<List<UserOptionValueResponse>>> listValuesByAttribute(
            @PathVariable Long attributeId) {
        try {
            List<UserOptionValueResponse> response =
                    optionAttributeService.listValuesByAttribute(attributeId);
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Lấy danh sách giá trị thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/attributes/" + attributeId + "/values"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(),
                            "/api/attributes/" + attributeId + "/values"));
        }
    }
}
