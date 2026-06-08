package com.example.the_cheaper.service.order;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.request.user.UserCreateOrderRequest;
import com.example.the_cheaper.dto.request.user.UserCheckoutRequest;
import com.example.the_cheaper.dto.response.user.UserOrderOverviewResponse;
import com.example.the_cheaper.dto.response.user.UserOrderResponse;
import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.user.UserOrderMapper;
import com.example.the_cheaper.repository.*;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserOrderMapper orderMapper;
    private final ProductVariantRepository productVariantRepository;
    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserOrderMapper orderMapper,
            ProductVariantRepository productVariantRepository,
            AddressRepository addressRepository,
            PaymentMethodRepository paymentMethodRepository,
            AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderMapper = orderMapper;
        this.productVariantRepository = productVariantRepository;
        this.addressRepository = addressRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.accountRepository = accountRepository;
    }

    // ─── Mua hàng (Thanh toán) ── POST /orders ───────────────────────────────────

    @Transactional
    public UserOrderResponse createOrder(Long accountId, UserCreateOrderRequest request) {
        // TODO: validate address belongs to user
        CartEntity cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotImplementedException("Giỏ hàng của người dùng không tồn tại"));

        String paymentCode = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("phương thức thanh toán đang bảo trì")).getCode();

        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotImplementedException("Tài khoản không tồn tại"));

        OrderEntity order = new OrderEntity();

        List<OrderItemEntity> orderItems = toOrderItemEntities(cart.getItems(), order);

        order.setItems(orderItems);
        order.setLocation(request.getLocation());
        order.setPhone(request.getPhone());
        order.setReceiver(request.getReceiver());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethodCode(paymentCode);
        order.setAccount(account);
        order.setFinalAmount(calculateFinalAmount(orderItems));
        order.setPaymentStatus(processPaymentStatus(paymentCode));
        processCart(cart);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    public OrderItemEntity toOrderItemEntity(CartItemEntity cartItem, OrderEntity order) {
        OrderItemEntity orderItem = orderMapper.toOrderItemEntity(cartItem);
        ProductVariantEntity variant = productVariantRepository.findById(cartItem.getVariant().getId())
                .orElseThrow(() -> new NotImplementedException("Phiên bản sản phẩm không tồn tại"));

        int orderedQty = cartItem.getQuantity();
        if (variant.getStock() < orderedQty) {
            throw new RuntimeException(
                    "Sản phẩm '" + variant.getSku() + "' không đủ tồn kho. " +
                    "Còn lại: " + variant.getStock() + ", yêu cầu: " + orderedQty);
        }

        // Trừ tồn kho và cộng số lượng đã bán
        variant.setStock(variant.getStock() - orderedQty);
        variant.setSold(variant.getSold() + orderedQty);
        productVariantRepository.save(variant);

        orderItem.setVariant(variant);
        orderItem.setOrder(order);
        return orderItem;
    }

    public void processCart(CartEntity cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public BigDecimal calculateFinalAmount(List<OrderItemEntity> cartItems) {
        return cartItems.stream()
                .map(item -> item.getVariant().getOverridePrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<OrderItemEntity> toOrderItemEntities(List<CartItemEntity> cartItems, OrderEntity order) {
        return cartItems.stream()
                .map(item -> toOrderItemEntity(item, order))
                .toList();
    }

    public int processPaymentStatus(String paymentMethodCode) {
        if (paymentMethodCode.equals("COD")) {
            return 0;
        }
        return 1;
    }

    @Transactional(readOnly = true)
    public Page<UserOrderResponse> getMyOrders(Long userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<OrderEntity> orderPage = orderRepository.findByAccountIdOrderByCreatedAtDesc(userId, pageable);
        return orderPage.map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserOrderResponse getOrderDetail(Long orderId, Long userId, String role) {
        // TODO: implement user ownership check
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotImplementedException("Đơn hàng không tồn tại"));
        if (!checkAuthentication(order.getAccount().getId(), userId, role)) {
            throw new NotImplementedException("Bạn không có quyền truy cập đơn hàng này");
        }
        return orderMapper.toResponse(order);
    }

    public boolean checkAuthentication(Long ownerId, Long userId, String role) {
        if (role.equals(Shared.ADMIN_ROLE)) {
            return true;
        }
        return ownerId.equals(userId);
    }

    @Transactional
    public UserOrderResponse cancelOrder(Long orderId, Long userId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));
        
        if (!order.getAccount().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }

        if (!"COD".equalsIgnoreCase(order.getPaymentMethodCode())) {
            throw new NotImplementedException("không thể hủy do chưa triển khai");
        }

        if (order.getStatus().equals(OrderStatus.CANCELED)) {
            throw new RuntimeException("Đơn hàng đã được hủy trước đó");
        }
        
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }
}
