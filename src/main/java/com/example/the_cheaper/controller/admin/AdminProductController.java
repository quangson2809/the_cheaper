package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.request.admin.AdminProductCreateRequest;
import com.example.the_cheaper.dto.request.admin.AdminProductFilterRequest;
import com.example.the_cheaper.dto.response.admin.AdminProductOverviewResponse;
import com.example.the_cheaper.dto.response.user.UserProductOverviewResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminProductUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminProductResponse;
import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.service.admin.AdminProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(Shared.BASE_URL_ADMIN)
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Page<AdminProductOverviewResponse>>> listProducts(
            AdminProductFilterRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            Page<AdminProductOverviewResponse> response = adminProductService.listProducts(currentUser,request);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách sản phẩm thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(), "/api/admin/products"));
        }
    }

    @GetMapping("/search-prodcuts")
    public ResponseEntity<ApiResponse<Page<AdminProductOverviewResponse>>> searchProducts(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @CurrentUser AccountEntity currentUser) {
        try {
            Page<AdminProductOverviewResponse> response = adminProductService.searchProductsByName(q, currentUser, page, limit);
            return ResponseEntity.ok(ApiResponse.success(response, "Tìm kiếm sản phẩm thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(),
                            "/api/products/search"));
        }
    }

    @PostMapping(value = "/products")
    public ResponseEntity<ApiResponse<AdminProductResponse>> createProduct(
            @RequestPart(value = "data") @Valid AdminProductCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminProductResponse response = adminProductService.createProduct(request, files, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Tạo sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/admin/products"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(), "/api/admin/products"));
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<AdminProductResponse>> getProductDetail(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminProductResponse response = adminProductService.getProductDetail(id, currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/products/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(), "/api/admin/products/" + id));
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<AdminProductResponse>> updateProduct(
            @PathVariable Long id,
            @RequestPart (value = "data",required = false) AdminProductUpdateRequest request,
            @RequestPart (value = "files", required = false) List<MultipartFile> files,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminProductResponse response = adminProductService.updateProduct(id, request, files, currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/products/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(), "/api/admin/products/" + id));
        }
    }

    @PatchMapping("/products/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(
            @PathVariable Long id,
            @RequestParam int status,
            @CurrentUser AccountEntity currentUser) {
        try {
            adminProductService.updateProductStatus(id, status, currentUser);
            return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật trạng thái sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/products/" + id + "/status"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(), "/api/admin/products/" + id + "/status"));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        try {
            adminProductService.deleteProduct(id, currentUser);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/products/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(), "/api/admin/products/" + id));
        }
    }
}


