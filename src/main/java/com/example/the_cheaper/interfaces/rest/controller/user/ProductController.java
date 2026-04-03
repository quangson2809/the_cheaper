package com.example.the_cheaper.interfaces.rest.controller.user;

import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.exception.ResourceNotFoundException;
import com.example.the_cheaper.interfaces.rest.dto.ApiResponse;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.ProductResponse;
import com.example.the_cheaper.application.usecase.product.BrowseProductUseCase;
import com.example.the_cheaper.application.usecase.product.ViewProductDetailUseCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final BrowseProductUseCase browseProductUseCase;
    private final ViewProductDetailUseCase viewProductDetailUseCase;

    public ProductController(BrowseProductUseCase browseProductUseCase,
            ViewProductDetailUseCase viewProductDetailUseCase) {
        this.browseProductUseCase = browseProductUseCase;
        this.viewProductDetailUseCase = viewProductDetailUseCase;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ProductResponse>>> browseProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            List<ProductResponse> response = browseProductUseCase.browseProducts(category, brand, page, limit);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách sản phẩm thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/products"));
        }
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<ApiResponse<ProductResponse>> viewProductDetail(
            @PathVariable("product_id") Long productId) {
        try {
            ProductResponse response = viewProductDetailUseCase.getProductDetail(productId);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/products/" + productId));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/products/" + productId));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            List<ProductResponse> response = browseProductUseCase.searchProducts(q, page, limit);
            return ResponseEntity.ok(ApiResponse.success(response, "Tìm kiếm sản phẩm thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/products/search"));
        }
    }

}
