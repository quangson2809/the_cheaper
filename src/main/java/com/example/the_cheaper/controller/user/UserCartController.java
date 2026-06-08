package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.user.UserAddCartItemRequest;
import com.example.the_cheaper.dto.request.user.UserUpdateCartItemRequest;
import com.example.the_cheaper.dto.response.user.UserCartOverviewResponse;
import com.example.the_cheaper.dto.response.user.UserCartResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.security.CurrentUser;
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

    // ─── 2.8 Xem thông tin giỏ hàng ─────────────────────────────────────────────
    // GET /carts/me

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

    // ─── 2.6 Thêm sản phẩm vào giỏ hàng ─────────────────────────────────────────
    // POST /carts/items

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

    // ─── 2.9 Xóa sản phẩm khỏi giỏ hàng ─────────────────────────────────────────
    // DELETE /carts/items/{id}

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

    // ─── 2.10 Sửa số lượng sản phẩm trong giỏ hàng ───────────────────────────────
    // PATCH /carts/items/{id}

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
}
