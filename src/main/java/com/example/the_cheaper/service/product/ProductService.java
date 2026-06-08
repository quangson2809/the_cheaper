package com.example.the_cheaper.service.product;

import com.example.the_cheaper.dto.request.user.UserProductFilterRequest;
import com.example.the_cheaper.dto.response.user.UserProductDetailResponse;
import com.example.the_cheaper.dto.response.user.UserProductOverviewResponse;
import com.example.the_cheaper.entity.ProductEntity;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.repository.ProductRepository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.the_cheaper.mapper.user.UserProductMapper;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserProductMapper userProductMapper;

    public ProductService(ProductRepository productRepository, UserProductMapper userProductMapper) {
        this.productRepository = productRepository;
        this.userProductMapper = userProductMapper;
    }

    @Transactional(readOnly = true)
    public Page<UserProductOverviewResponse> browseProducts(UserProductFilterRequest request) {
        if ( request.getPage() < 1) {
            throw new IllegalArgumentException("Page must be >= 1");
        }
        if ( request.getLimit() <= 0 || request.getLimit() > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        return productRepository.findActiveProductsByUserFilter(
                request.getCategoryId(),
                request.getBrandId(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getSortBy(),
                PageRequest.of(request.getPage() - 1, request.getLimit()))
                .map(userProductMapper::toOverviewResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserProductOverviewResponse> searchProducts(
            String query, int page, int limit) {
        return productRepository.findActiveProductsByNameContainingIgnoreCase(
                query, PageRequest.of(page - 1, limit))
                .map(userProductMapper::toOverviewResponse);
    }

    @Transactional(readOnly = true)
    public UserProductDetailResponse getProductDetail(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sản phẩm không tồn tại"));

        if (!product.isAvailable()) {
            throw new ResourceNotFoundException(
                    "Sản phẩm đã ngừng kinh doanh hoặc không tồn tại");
        }

        return userProductMapper.toDetailResponse(product);
    }
}
