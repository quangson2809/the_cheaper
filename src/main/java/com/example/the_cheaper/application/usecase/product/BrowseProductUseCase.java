package com.example.the_cheaper.application.usecase.product;

import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.ProductResponse;
import com.example.the_cheaper.domain.repository.ProductRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BrowseProductUseCase {

    private final ProductRepository productRepository;

    public BrowseProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> browseProducts(String category, String brand, int page, int limit) {
        throw new NotImplementedException("Chức năng duyệt sản phẩm chưa được triển khai");
    }

    public List<ProductResponse> searchProducts(String query, int page, int limit) {
        throw new NotImplementedException("Chức năng tìm kiếm sản phẩm chưa được triển khai");
    }
}
