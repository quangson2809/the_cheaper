package com.example.the_cheaper.service.order;

import com.example.the_cheaper.dto.request.user.UserCreateOrderRequest;
import com.example.the_cheaper.dto.response.user.UserOrderResponse;
import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.user.UserOrderMapper;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.CartRepository;
import com.example.the_cheaper.repository.OrderRepository;
import com.example.the_cheaper.repository.PaymentMethodRepository;
import com.example.the_cheaper.repository.ProductVariantRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserOrderMapper orderMapper;
    private final ProductVariantRepository productVariantRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserOrderMapper orderMapper,
            ProductVariantRepository productVariantRepository,
            PaymentMethodRepository paymentMethodRepository,
            AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderMapper = orderMapper;
        this.productVariantRepository = productVariantRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    @PreAuthorize("@orderAccess.canAccessOwn(authentication, #p0, 'USER_ORDER_CREATE')")
    public UserOrderResponse createOrder(Long accountId, UserCreateOrderRequest request) {
        CartEntity cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng của người dùng không tồn tại"));

        String paymentCode = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("Phương thức thanh toán không tồn tại"))
                .getCode();

        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));

        OrderEntity order = OrderEntity.builder()
                .location(request.getLocation())
                .phone(request.getPhone())
                .receiver(request.getReceiver())
                .status(OrderStatus.PENDING)
                .paymentMethodCode(paymentCode)
                .account(account)
                .paymentStatus(processPaymentStatus(paymentCode))
                .build();

        List<OrderItemEntity> orderItems = toOrderItemEntities(cart.getItems(), order);
        order.addItems(orderItems);
        order.recalculateFinalAmount();
        processCart(cart);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    private OrderItemEntity toOrderItemEntity(CartItemEntity cartItem, OrderEntity order) {
        OrderItemEntity orderItem = orderMapper.toOrderItemEntity(cartItem);
        ProductVariantEntity variant = productVariantRepository.findById(cartItem.getVariant().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Phiên bản sản phẩm không tồn tại"));

        int orderedQty = cartItem.getQuantity();
        if (variant.getStock() < orderedQty) {
            throw new InvalidInputException(
                    "Sản phẩm '" + variant.getSku() + "' không đủ tồn kho. " +
                    "Còn lại: " + variant.getStock() + ", yêu cầu: " + orderedQty);
        }

        variant.setStock(variant.getStock() - orderedQty);
        variant.setSold(variant.getSold() + orderedQty);
        productVariantRepository.save(variant);

        orderItem.setVariant(variant);
        orderItem.setOrder(order);
        return orderItem;
    }

    private void processCart(CartEntity cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public BigDecimal calculateFinalAmount(List<OrderItemEntity> cartItems) {
        return cartItems.stream()
                .map(item -> item.getVariant().getOverridePrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItemEntity> toOrderItemEntities(List<CartItemEntity> cartItems, OrderEntity order) {
        return cartItems.stream()
                .map(item -> toOrderItemEntity(item, order))
                .toList();
    }

    private int processPaymentStatus(String paymentMethodCode) {
        return "COD".equals(paymentMethodCode) ? 0 : 1;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@orderAccess.canAccessOwn(authentication, #p0, 'USER_ORDER_READ')")
    public Page<UserOrderResponse> getMyOrders(Long accountId, int page, int limit) {
        if (page < 1 || limit < 1 || limit > 100) {
            throw new InvalidInputException("page phải >= 1; limit phải từ 1 đến 100");
        }
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<OrderEntity> orderPage = orderRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable);
        return orderPage.map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@orderAccess.canAccessOwn(authentication, #p1, 'USER_ORDER_READ')")
    public UserOrderResponse getOrderDetail(Long orderId, Long accountId) {
        OrderEntity order = orderRepository.findByIdAndAccountId(orderId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));
        return orderMapper.toResponse(order);
    }

    @Transactional
    @PreAuthorize("@orderAccess.canAccessOwn(authentication, #p1, 'USER_ORDER_CANCEL')")
    public UserOrderResponse cancelOrder(Long orderId, Long accountId) {
        OrderEntity order = orderRepository.findByIdAndAccountId(orderId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        if (!"COD".equalsIgnoreCase(order.getPaymentMethodCode())) {
            throw new NotImplementedException("Không thể hủy đơn hàng với phương thức thanh toán này");
        }

        try {
            order.transitionTo(OrderStatus.CANCELED);
        } catch (IllegalStateException e) {
            throw new InvalidInputException(e.getMessage());
        }

        orderRepository.save(order);
        return orderMapper.toResponse(order);
    }
}
