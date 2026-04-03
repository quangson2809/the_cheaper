package com.example.the_cheaper.interfaces.rest.controller.user;

import com.example.the_cheaper.domain.exception.InvalidInputException;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.exception.ResourceNotFoundException;
import com.example.the_cheaper.interfaces.rest.dto.ApiResponse;
import com.example.the_cheaper.interfaces.rest.dto.request.user.ReviewRequest;
import com.example.the_cheaper.interfaces.rest.dto.response.user.ReviewResponse;
import com.example.the_cheaper.application.usecase.product.ReviewUseCase;
import com.example.the_cheaper.interfaces.rest.mapper.user.ReviewMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ReviewController {

    private final ReviewUseCase reviewUseCase;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewUseCase reviewUseCase, ReviewMapper reviewMapper) {
        this.reviewUseCase = reviewUseCase;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping("/{product_id}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(@RequestParam Long userId,
            @PathVariable("product_id") Long productId,
            @RequestBody ReviewRequest request) {
        try {
            ReviewResponse response = reviewUseCase.createReview(reviewMapper.toCommand(userId, productId, request));
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Tạo đánh giá thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/products/" + productId + "/reviews"));
        } catch (InvalidInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                            "/api/products/" + productId + "/reviews"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(),
                            "/api/products/" + productId + "/reviews"));
        }
    }

    @GetMapping("/{product_id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getProductReviews(
            @PathVariable("product_id") Long productId) {
        try {
            List<ReviewResponse> response = reviewUseCase.getProductReviews(productId);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách đánh giá thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/products/" + productId + "/reviews"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(),
                            "/api/products/" + productId + "/reviews"));
        }
    }

}
