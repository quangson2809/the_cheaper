package com.example.the_cheaper.service.product;

import com.example.the_cheaper.dto.request.user.UserReviewRequest;
import com.example.the_cheaper.dto.response.user.UserReviewResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.ProductEntity;
import com.example.the_cheaper.entity.ReviewEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.user.UserReviewMapper;
import com.example.the_cheaper.repository.OrderRepository;
import com.example.the_cheaper.repository.ProductRepository;
import com.example.the_cheaper.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserReviewMapper userReviewMapper;

    @Transactional
    public UserReviewResponse createReview(AccountEntity currentUser, Long productId, UserReviewRequest request) {
        // 1. Kiểm tra sản phẩm tồn tại
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id: " + productId));

        // 2. Kiểm tra user có đơn hàng DELIVERED + đã thanh toán chứa sản phẩm này không
        boolean hasPurchased = orderRepository.existsDeliveredPaidOrderByAccountAndProduct(
                currentUser.getId(), productId);
        if (!hasPurchased) {
            throw new InvalidInputException(
                    "Bạn cần mua và nhận sản phẩm thành công trước khi có thể đánh giá");
        }

        // 3. Kiểm tra user đã review sản phẩm này chưa
        boolean alreadyReviewed = reviewRepository.existsByAccountIdAndProductId(
                currentUser.getId(), productId);
        if (alreadyReviewed) {
            throw new InvalidInputException("Bạn đã đánh giá sản phẩm này rồi");
        }

        // 4. Tạo và lưu review
        ReviewEntity review = ReviewEntity.builder()
                .account(currentUser)
                .product(product)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        return userReviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public List<UserReviewResponse> getProductReviews(Long productId) {
        // Kiểm tra sản phẩm tồn tại
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với id: " + productId);
        }

        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(userReviewMapper::toResponse)
                .collect(Collectors.toList());
    }
}



