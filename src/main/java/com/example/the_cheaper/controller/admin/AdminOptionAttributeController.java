package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminOptionAttributeRequest;
import com.example.the_cheaper.dto.request.admin.AdminOptionValueRequest;
import com.example.the_cheaper.dto.response.admin.AdminOptionAttributeResponse;
import com.example.the_cheaper.dto.response.admin.AdminOptionValueResponse;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.service.admin.AdminOptionAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.security.CurrentUser;

import java.util.List;

/**
 * Admin API: quản lý OptionAttribute (thuộc tính sản phẩm như: Size, Màu sắc, Chất liệu…)
 * và OptionValue (giá trị của từng thuộc tính như: S, M, L; Đỏ, Xanh…).
 *
 * Tất cả endpoint yêu cầu userId + role = ADMIN (kiểm tra trong service qua AdminProtectedAccess).
 *
 * Base URL: /api/admin
 */
@RestController
@RequestMapping(Shared.BASE_URL_ADMIN)
@RequiredArgsConstructor
public class AdminOptionAttributeController {

    private final AdminOptionAttributeService optionAttributeService;

    // ─────────────────────────────────────────────────────────────
    // OPTION ATTRIBUTE ENDPOINTS
    // ─────────────────────────────────────────────────────────────

    /**
     * GET /api/admin/option-attributes
     * Lấy danh sách tất cả thuộc tính (kèm danh sách value của mỗi thuộc tính).
     */
    @GetMapping("/option-attributes")
    public ResponseEntity<ApiResponse<List<AdminOptionAttributeResponse>>> listOptionAttributes(
            @CurrentUser AccountEntity currentUser) {
        try {
            List<AdminOptionAttributeResponse> response =
                    optionAttributeService.listOptionAttributes(currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách thuộc tính thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes"));
        }
    }

    /**
     * POST /api/admin/option-attributes
     * Tạo mới một thuộc tính (có thể kèm danh sách values ban đầu).
     */
    @PostMapping("/option-attributes")
    public ResponseEntity<ApiResponse<AdminOptionAttributeResponse>> createOptionAttribute(
            @Valid @RequestBody AdminOptionAttributeRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminOptionAttributeResponse response =
                    optionAttributeService.createOptionAttribute(request, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Tạo thuộc tính thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes"));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage(),
                            "/api/admin/option-attributes"));
        }
    }

    /**
     * GET /api/admin/option-attributes/{id}
     * Lấy chi tiết một thuộc tính (kèm danh sách values).
     */
    @GetMapping("/option-attributes/{id}")
    public ResponseEntity<ApiResponse<AdminOptionAttributeResponse>> getOptionAttributeDetail(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminOptionAttributeResponse response =
                    optionAttributeService.getOptionAttributeDetail(id, currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết thuộc tính thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        }
    }

    /**
     * PUT /api/admin/option-attributes/{id}
     * Cập nhật tên thuộc tính và toàn bộ values (replace strategy).
     */
    @PutMapping("/option-attributes/{id}")
    public ResponseEntity<ApiResponse<AdminOptionAttributeResponse>> updateOptionAttribute(
            @PathVariable Long id,
            @Valid @RequestBody AdminOptionAttributeRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminOptionAttributeResponse response =
                    optionAttributeService.updateOptionAttribute(id, request, currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật thuộc tính thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        }
    }

    /**
     * DELETE /api/admin/option-attributes/{id}
     * Xóa thuộc tính và toàn bộ values liên kết (cascade).
     */
    @DeleteMapping("/option-attributes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOptionAttribute(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        try {
            optionAttributeService.deleteOptionAttribute(id, currentUser);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa thuộc tính thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // OPTION VALUE ENDPOINTS (nested under attribute)
    // ─────────────────────────────────────────────────────────────

    /**
     * GET /api/admin/option-attributes/{attributeId}/values
     * Lấy danh sách các value của một thuộc tính cụ thể.
     */
    @GetMapping("/option-attributes/{attributeId}/values")
    public ResponseEntity<ApiResponse<List<AdminOptionValueResponse>>> listValuesByAttribute(
            @PathVariable Long attributeId,
            @CurrentUser AccountEntity currentUser) {
        try {
            List<AdminOptionValueResponse> response =
                    optionAttributeService.listValuesByAttribute(attributeId, currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách giá trị thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values"));
        }
    }

    /**
     * POST /api/admin/option-attributes/{attributeId}/values
     * Thêm một value mới vào thuộc tính đã có.
     */
    @PostMapping("/option-attributes/{attributeId}/values")
    public ResponseEntity<ApiResponse<AdminOptionValueResponse>> addValueToAttribute(
            @PathVariable Long attributeId,
            @Valid @RequestBody AdminOptionValueRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminOptionValueResponse response =
                    optionAttributeService.addValueToAttribute(attributeId, request, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Thêm giá trị thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values"));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values"));
        }
    }

    /**
     * DELETE /api/admin/option-attributes/{attributeId}/values/{valueId}
     * Xóa một value khỏi thuộc tính.
     */
    @DeleteMapping("/option-attributes/{attributeId}/values/{valueId}")
    public ResponseEntity<ApiResponse<Void>> deleteValue(
            @PathVariable Long attributeId,
            @PathVariable Long valueId,
            @CurrentUser AccountEntity currentUser) {
        try {
            optionAttributeService.deleteValue(attributeId, valueId, currentUser);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa giá trị thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values/" + valueId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values/" + valueId));
        }
    }
}
