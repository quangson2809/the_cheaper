package com.example.the_cheaper.application.usecase.product;

import com.example.the_cheaper.application.command.CreateReviewCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.response.user.ReviewResponse;
import com.example.the_cheaper.domain.repository.ProductRepository;
import com.example.the_cheaper.domain.repository.ReviewRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewUseCase {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewUseCase(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    public ReviewResponse createReview(CreateReviewCommand command) {
        throw new NotImplementedException("Chức năng tạo đánh giá chưa được triển khai");
    }

    public List<ReviewResponse> getProductReviews(Long productId) {
        throw new NotImplementedException("Chức năng lấy đánh giá sản phẩm chưa được triển khai");
    }

}
