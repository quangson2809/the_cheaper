package com.example.the_cheaper.application.usecase.product;

import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.ProductResponse;
import com.example.the_cheaper.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ViewProductDetailUseCase {

    private final ProductRepository productRepository;

    public ViewProductDetailUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse getProductDetail(Long productId) {
        throw new NotImplementedException("Chức năng xem chi tiết sản phẩm chưa được triển khai");
    }

}


