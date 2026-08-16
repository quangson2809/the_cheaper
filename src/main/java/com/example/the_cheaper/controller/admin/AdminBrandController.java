package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminBrandRequest;
import com.example.the_cheaper.dto.response.admin.AdminAccountResponse;
import com.example.the_cheaper.dto.response.admin.AdminBrandResponse;
import com.example.the_cheaper.service.admin.AdminBrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.annotation.CurrentUser;
import java.util.List;

@RestController
@RequestMapping(Shared.BASE_URL_ADMIN)
@RequiredArgsConstructor
public class AdminBrandController {

    private final AdminBrandService adminBrandService;

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<AdminBrandResponse>>> listBrands(
            @CurrentUser AccountEntity currentUser) {
        List<AdminBrandResponse> response = adminBrandService.listBrands(currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách thương hiệu thành công"));
    }

    @GetMapping("/brands/search")
    public ResponseEntity<ApiResponse<Page<AdminBrandResponse>>> searchProducts(
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @CurrentUser AccountEntity currentUser
    ) {
        try {
            Page<AdminBrandResponse> response = adminBrandService.searchBrands(name, currentUser, page, limit);
            return ResponseEntity.ok(ApiResponse.success(response, "Tìm tài khoản thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(),
                            "/api/accounts/search"));
        }
    }

    @PostMapping("/brands")
    public ResponseEntity<ApiResponse<AdminBrandResponse>> createBrand(
            @Valid @RequestBody AdminBrandRequest request,
            @CurrentUser AccountEntity currentUser) {
        AdminBrandResponse response = adminBrandService.createBrand(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tạo thương hiệu thành công"));
    }

    @GetMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<AdminBrandResponse>> getBrandDetail(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        AdminBrandResponse response = adminBrandService.getBrandDetail(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết thương hiệu thành công"));
    }

    @PutMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<AdminBrandResponse>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody AdminBrandRequest request,
            @CurrentUser AccountEntity currentUser) {
        AdminBrandResponse response = adminBrandService.updateBrand(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật thương hiệu thành công"));
    }

    @PatchMapping("/brands/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateBrandStatus(
            @PathVariable Long id,
            @RequestParam int status,
            @CurrentUser AccountEntity currentUser) {
        adminBrandService.updateBrandStatus(id, status, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật thương hiệu thành công"));
    }

    @DeleteMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        adminBrandService.deleteBrand(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa thương hiệu thành công"));
    }
}


