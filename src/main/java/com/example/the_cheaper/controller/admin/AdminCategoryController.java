package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminCategoryRequest;
import com.example.the_cheaper.dto.response.admin.AdminBrandResponse;
import com.example.the_cheaper.dto.response.admin.AdminCategoryResponse;
import com.example.the_cheaper.service.admin.AdminCategoryService;
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
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<AdminCategoryResponse>>> listCategories(
            @CurrentUser AccountEntity currentUser) {
        List<AdminCategoryResponse> response = adminCategoryService.listCategories(currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách danh mục thành công"));
    }

    @GetMapping("/categories/search")
    public ResponseEntity<ApiResponse<Page<AdminCategoryResponse>>> searchProducts(
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @CurrentUser AccountEntity currentUser
    ) {
        try {
            Page<AdminCategoryResponse> response = adminCategoryService.searchCategories(name, currentUser, page, limit);
            return ResponseEntity.ok(ApiResponse.success(response, "Tìm thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(),
                            "/api/accounts/search"));
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> createCategory(
            @Valid @RequestBody AdminCategoryRequest request,
            @CurrentUser AccountEntity currentUser) {
        AdminCategoryResponse response = adminCategoryService.createCategory(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tạo danh mục thành công"));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> getCategoryDetail(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        AdminCategoryResponse response = adminCategoryService.getCategoryDetail(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết danh mục thành công"));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody AdminCategoryRequest request,
            @CurrentUser AccountEntity currentUser) {
        AdminCategoryResponse response = adminCategoryService.updateCategory(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật danh mục thành công"));
    }

    @PatchMapping("/categories/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateCategoryStatus(
            @PathVariable Long id,
            @RequestParam int status,
            @CurrentUser AccountEntity currentUser) {
        adminCategoryService.updateCategoryStatus(id, status, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật danh mục thành công"));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        adminCategoryService.deleteCategory(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa danh mục thành công"));
    }

}


