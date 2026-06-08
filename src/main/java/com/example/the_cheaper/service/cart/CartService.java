package com.example.the_cheaper.service.cart;

import com.example.the_cheaper.dto.request.user.UserAddCartItemRequest;
import com.example.the_cheaper.dto.request.user.UserUpdateCartItemRequest;
import com.example.the_cheaper.dto.response.user.UserCartItemResponse;
import com.example.the_cheaper.dto.response.user.UserCartOverviewResponse;
import com.example.the_cheaper.dto.response.user.UserCartResponse;
import com.example.the_cheaper.entity.CartEntity;
import com.example.the_cheaper.entity.CartItemEntity;
import com.example.the_cheaper.entity.ProductVariantEntity;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.user.UserCartMapper;
import com.example.the_cheaper.repository.CartItemRepository;
import com.example.the_cheaper.repository.CartRepository;
import com.example.the_cheaper.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository variantRepository;
    private final UserCartMapper cartMapper;

    public CartService(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductVariantRepository variantRepository,
            UserCartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.variantRepository = variantRepository;
        this.cartMapper = cartMapper;
    }


    @Transactional(readOnly = true)
    public UserCartResponse getMyCart(Long accountId) {
        CartEntity cart = cartRepository.findByAccountId(accountId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho tài khoản"));
        return cartMapper.toResponse(cart);
    }


    @Transactional
    public UserCartOverviewResponse addCartItem(Long accountId,UserAddCartItemRequest request) {
        CartEntity cart = cartRepository.findByAccountId(accountId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy giỏ hàng cho tài khoản"));

        ProductVariantEntity variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm"));

        if(!variant.isInStock()){
            throw new IllegalArgumentException("Sản phẩm đã hết hàng hoặc không đủ số lượng yêu cầu");
        }

        if(cart.getItems().stream().anyMatch(item -> item.getVariant().getId().equals(request.getVariantId()))){
            cart.getItems().stream()
                    .filter(item -> item.getVariant().getId().equals(request.getVariantId()))
                    .findFirst()
                    .ifPresent(item -> {
                        int newQuantity = item.getQuantity() + request.getQuantity();
                        if(newQuantity > variant.getStock()){
                            throw new IllegalArgumentException("Số lượng trong giỏ hàng vượt quá số lượng tồn kho");
                        }
                        item.setQuantity(newQuantity);
                    });
        }
        else{
            CartItemEntity cartItemEntity = cartMapper.toCartItemEntity(request.getVariantId(), request.getQuantity());
            cartItemEntity.setCart(cart);
            cartItemEntity.setVariant(variant);
            cart.getItems().add(cartItemEntity);
        }

        return cartMapper.toOverviewResponse(cartRepository.save(cart));
    }

    @Transactional
    public UserCartResponse updateCartItem(Long accountId, Long cartItemId, UserUpdateCartItemRequest request) {
        CartEntity cart = cartRepository.findByAccountId(accountId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho tài khoản"));

        cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .ifPresent(item -> {
                    if(request.getQuantity() > item.getVariant().getStock()){
                        throw new IllegalArgumentException("Số lượng trong giỏ hàng vượt quá số lượng tồn kho");
                    }
                    item.setQuantity(request.getQuantity());
                });

        return cartMapper.toResponse(cartRepository.save(cart));
    }


    @Transactional
    public UserCartResponse removeCartItem(Long accountId, Long cartItemId) {
        CartEntity cart = cartRepository.findByAccountId(accountId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho tài khoản"));

        CartItemEntity itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));

        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        return cartMapper.toResponse(cartRepository.save(cart));
    }

}
