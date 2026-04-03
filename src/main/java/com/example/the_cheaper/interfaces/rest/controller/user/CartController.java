package com.example.the_cheaper.interfaces.rest.controller.user;

import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.exception.ResourceNotFoundException;
import com.example.the_cheaper.interfaces.rest.dto.ApiResponse;
import com.example.the_cheaper.interfaces.rest.dto.request.user.CartRequest;
import com.example.the_cheaper.interfaces.rest.dto.response.user.CartItemResponse;
import com.example.the_cheaper.application.usecase.cart.CartUseCase;
import com.example.the_cheaper.interfaces.rest.mapper.user.CartMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartUseCase cartUseCase;
    private final CartMapper cartMapper;

    public CartController(CartUseCase cartUseCase, CartMapper cartMapper) {
        this.cartUseCase = cartUseCase;
        this.cartMapper = cartMapper;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> viewCart(@RequestParam Long userId) {
        try {
            List<CartItemResponse> response = cartUseCase.getCartItems(userId);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy giỏ hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/cart"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/cart"));
        }
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemResponse>> addItemToCart(@RequestParam Long userId,
            @RequestBody CartRequest request) {
        try {
            CartItemResponse response = cartUseCase.addToCart(cartMapper.toAddCommand(userId, request));
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Thêm sản phẩm vào giỏ hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/cart/items"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/cart/items"));
        }
    }

    @PutMapping("/items/{cart_item_id}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItem(@RequestParam Long userId,
            @PathVariable("cart_item_id") Long cartItemId,
            @RequestBody CartRequest request) {
        try {
            CartItemResponse response = cartUseCase.updateCartItem(cartMapper.toUpdateCommand(userId, cartItemId, request));
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật giỏ hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/cart/items/" + cartItemId));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/cart/items/" + cartItemId));
        }
    }

    @DeleteMapping("/items/{cart_item_id}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(@RequestParam Long userId,
            @PathVariable("cart_item_id") Long cartItemId) {
        try {
            cartUseCase.removeFromCart(userId, cartItemId);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa sản phẩm khỏi giỏ hàng thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/cart/items/" + cartItemId));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/cart/items/" + cartItemId));
        }
    }

    @DeleteMapping()
    public ResponseEntity<ApiResponse<Void>> clearCart(@RequestParam Long userId) {
        try {
            cartUseCase.clearCart(userId);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa giỏ hàng thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/cart"));
        }
    }

}
