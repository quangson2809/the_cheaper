package com.example.the_cheaper.application.usecase.cart;

import com.example.the_cheaper.application.command.AddToCartCommand;
import com.example.the_cheaper.application.command.UpdateCartItemCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.response.user.CartItemResponse;
import com.example.the_cheaper.domain.repository.CartRepository;
import com.example.the_cheaper.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartUseCase {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartUseCase(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<CartItemResponse> getCartItems(Long userId) {
        throw new NotImplementedException("Chức năng lấy giỏ hàng chưa được triển khai");
    }

    public CartItemResponse addToCart(AddToCartCommand command) {
        throw new NotImplementedException("Chức năng thêm vào giỏ hàng chưa được triển khai");
    }

    public CartItemResponse updateCartItem(UpdateCartItemCommand command) {
        throw new NotImplementedException("Chức năng cập nhật giỏ hàng chưa được triển khai");
    }

    public void removeFromCart(Long userId, Long cartItemId) {
        throw new NotImplementedException("Chức năng xóa khỏi giỏ hàng chưa được triển khai");
    }

    public void clearCart(Long userId) {
        throw new NotImplementedException("Chức năng xóa giỏ hàng chưa được triển khai");
    }
}
