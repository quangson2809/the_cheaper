package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.user.UserReviewRequest;
import com.example.the_cheaper.dto.response.user.UserReviewResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.security.CurrentUser;
import com.example.the_cheaper.service.product.ReviewService;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class UserReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{product_id}/reviews")
    public ResponseEntity<ApiResponse<UserReviewResponse>> createReview(
            @CurrentUser AccountEntity currentUser,
            @PathVariable("product_id") Long productId,
            @Valid @RequestBody UserReviewRequest request) {
        try {
            UserReviewResponse response = reviewService.createReview(currentUser, productId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Tạo đánh giá thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/products/" + productId + "/reviews"));
        } catch (InvalidInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                            "/api/products/" + productId + "/reviews"));
        }
    }

    @GetMapping("/{product_id}/reviews")
    public ResponseEntity<ApiResponse<List<UserReviewResponse>>> getProductReviews(
            @PathVariable("product_id") Long productId) {
        try {
            List<UserReviewResponse> response = reviewService.getProductReviews(productId);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách đánh giá thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/products/" + productId + "/reviews"));
        }
    }
}
