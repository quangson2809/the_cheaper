package com.example.the_cheaper.interfaces.rest.controller.admin;

import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.domain.exception.ResourceNotFoundException;
import com.example.the_cheaper.interfaces.rest.dto.ApiResponse;
import com.example.the_cheaper.interfaces.rest.dto.request.admin.BrandRequest;
import com.example.the_cheaper.interfaces.rest.dto.request.admin.CategoryRequest;
import com.example.the_cheaper.interfaces.rest.dto.request.admin.ProductRequest;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.BrandResponse;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.CategoryResponse;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.ProductResponse;
import com.example.the_cheaper.application.usecase.admin.ManageBrandUseCase;
import com.example.the_cheaper.application.usecase.admin.ManageCategoryUseCase;
import com.example.the_cheaper.application.usecase.admin.ManageProductUseCase;
import com.example.the_cheaper.interfaces.rest.mapper.admin.BrandMapper;
import com.example.the_cheaper.interfaces.rest.mapper.admin.CategoryMapper;
import com.example.the_cheaper.interfaces.rest.mapper.admin.ProductMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminProductController {

    private final ManageProductUseCase manageProductUseCase;
    private final ManageBrandUseCase manageBrandUseCase;
    private final ManageCategoryUseCase manageCategoryUseCase;
    private final ProductMapper productMapper;
    private final BrandMapper brandMapper;
    private final CategoryMapper categoryMapper;

    public AdminProductController(ManageProductUseCase manageProductUseCase, ManageBrandUseCase manageBrandUseCase,
            ManageCategoryUseCase manageCategoryUseCase, ProductMapper productMapper, BrandMapper brandMapper,
            CategoryMapper categoryMapper) {
        this.manageProductUseCase = manageProductUseCase;
        this.manageBrandUseCase = manageBrandUseCase;
        this.manageCategoryUseCase = manageCategoryUseCase;
        this.productMapper = productMapper;
        this.brandMapper = brandMapper;
        this.categoryMapper = categoryMapper;
    }

    // ==================== PRODUCTS ====================

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ProductResponse> response = manageProductUseCase.listProducts(page, limit).stream()
                    .map(productMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách sản phẩm thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/products"));
        }
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductRequest request) {
        try {
            ProductResponse response = productMapper.toResponse(manageProductUseCase.createProduct(productMapper.toCommand(request)));
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Tạo sản phẩm thành công"));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/admin/products"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/products"));
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(@PathVariable Long id) {
        try {
            ProductResponse response = productMapper.toResponse(manageProductUseCase.getProductDetail(id));
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/products/" + id));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/products/" + id));
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id,
            @RequestBody ProductRequest request) {
        try {
            ProductResponse response = productMapper.toResponse(manageProductUseCase.updateProduct(productMapper.toCommand(id, request)));
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/products/" + id));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/products/" + id));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        try {
            manageProductUseCase.deleteProduct(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/products/" + id));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/products/" + id));
        }
    }

    // ==================== CATEGORIES ====================

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> listCategories() {
        try {
            List<CategoryResponse> response = manageCategoryUseCase.listCategories().stream()
                    .map(categoryMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách danh mục thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/categories"));
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody CategoryRequest request) {
        try {
            CategoryResponse response = categoryMapper.toResponse(manageCategoryUseCase.createCategory(productMapper.toCommand(request)));
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Tạo danh mục thành công"));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/admin/categories"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/categories"));
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id,
            @RequestBody CategoryRequest request) {
        try {
            CategoryResponse response = categoryMapper.toResponse(manageCategoryUseCase.updateCategory(productMapper.toCommand(id, request)));
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật danh mục thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/categories/" + id));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/categories/" + id));
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        try {
            manageCategoryUseCase.deleteCategory(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa danh mục thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/categories/" + id));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/categories/" + id));
        }
    }

    // ==================== BRANDS ====================

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> listBrands() {
        try {
            List<BrandResponse> response = manageBrandUseCase.listBrands().stream()
                    .map(brandMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách thương hiệu thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/brands"));
        }
    }

    @PostMapping("/brands")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@RequestBody BrandRequest request) {
        try {
            BrandResponse response = brandMapper.toResponse(manageBrandUseCase.createBrand(productMapper.toCommand(request)));
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Tạo thương hiệu thành công"));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/admin/brands"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/brands"));
        }
    }

    @PutMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(@PathVariable Long id,
            @RequestBody BrandRequest request) {
        try {
            BrandResponse response = brandMapper.toResponse(manageBrandUseCase.updateBrand(productMapper.toCommand(id, request)));
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật thương hiệu thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/brands/" + id));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/brands/" + id));
        }
    }

    @DeleteMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        try {
            manageBrandUseCase.deleteBrand(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa thương hiệu thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/admin/brands/" + id));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/brands/" + id));
        }
    }

}
