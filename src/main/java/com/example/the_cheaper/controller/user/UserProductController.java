package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.user.UserProductFilterRequest;
import com.example.the_cheaper.dto.response.user.UserProductDetailResponse;
import com.example.the_cheaper.dto.response.user.UserProductOverviewResponse;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.service.product.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class UserProductController {

    private final ProductService productService;

    public UserProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserProductOverviewResponse>>> browseProducts(
            UserProductFilterRequest request) {
        try {
            Page<UserProductOverviewResponse> response = productService.browseProducts(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách sản phẩm thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(), "/api/products"));
        }
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<ApiResponse<UserProductDetailResponse>> viewProductDetail(
            @PathVariable("product_id") Long productId) {
        try {
            UserProductDetailResponse response = productService.getProductDetail(productId);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/products/" + productId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(),
                            "/api/products/" + productId));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserProductOverviewResponse>>> searchProducts(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            Page<UserProductOverviewResponse> response = productService.searchProducts(q, page, limit);
            return ResponseEntity.ok(ApiResponse.success(response, "Tìm kiếm sản phẩm thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(),
                            "/api/products/search"));
        }
    }
}

