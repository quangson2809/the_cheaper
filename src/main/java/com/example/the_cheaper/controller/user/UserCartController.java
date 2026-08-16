package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.user.UserAddCartItemRequest;
import com.example.the_cheaper.dto.request.user.UserMergeCartRequest;
import com.example.the_cheaper.dto.request.user.UserUpdateCartItemRequest;
import com.example.the_cheaper.dto.response.user.UserCartOverviewResponse;
import com.example.the_cheaper.dto.response.user.UserCartResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class UserCartController {

    private final CartService cartService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserCartResponse>> getMyCart(
            @CurrentUser AccountEntity currentUser) {
        try {
            UserCartResponse response = cartService.getMyCart(currentUser.getId());
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Lấy giỏ hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/carts/me"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/carts/me"));
        }
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<UserCartOverviewResponse>> addCartItem(
            @CurrentUser AccountEntity currentUser,
            @Valid @RequestBody UserAddCartItemRequest request) {
        try {
            UserCartOverviewResponse response = cartService.addCartItem(currentUser.getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Thêm sản phẩm vào giỏ hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/carts/items"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/carts/items"));
        }
    }


    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<UserCartResponse>> removeCartItem(
            @CurrentUser AccountEntity currentUser,
            @PathVariable Long id) {
        try {
            UserCartResponse response = cartService.removeCartItem(currentUser.getId(), id);
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Xóa sản phẩm khỏi giỏ hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/carts/items/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/carts/items/" + id));
        }
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<ApiResponse<UserCartResponse>> updateCartItem(
            @CurrentUser AccountEntity currentUser,
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateCartItemRequest request) {
        try {
            UserCartResponse response = cartService.updateCartItem(currentUser.getId(), id, request);
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Cập nhật số lượng sản phẩm thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/carts/items/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/carts/items/" + id));
        }
    }

    @PostMapping("/merge")
    public ResponseEntity<ApiResponse<Void>> mergeCart(
            @CurrentUser AccountEntity currentUser,
            @RequestBody UserMergeCartRequest guestCart) {
        try {
            cartService.mergeCart(currentUser.getId(), guestCart);
            return ResponseEntity.ok(
                    ApiResponse.success(null, "Gộp giỏ hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/carts/merge"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/carts/merge"));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<UserCartOverviewResponse>> getCartCount(
            @CurrentUser AccountEntity currentUser) {
        try {
            UserCartOverviewResponse response = cartService.getCartCount(currentUser.getId());
            return ResponseEntity.ok(
                    ApiResponse.success(response, "Lấy số lượng giỏ hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/carts/count"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/carts/me"));
        }
    }
}
