package com.example.the_cheaper.integration.service;

import com.example.the_cheaper.dto.request.user.UserCreateOrderRequest;
import com.example.the_cheaper.dto.response.user.UserOrderResponse;
import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.entity.OrderStatus;
import com.example.the_cheaper.repository.*;
import com.example.the_cheaper.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrderServiceIntegrationTest {

    @Autowired private OrderService orderService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductVariantRepository productVariantRepository;
    @Autowired private PaymentMethodRepository paymentMethodRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private AccountEntity account;
    private ProductVariantEntity variant;
    private PaymentMethodEntity paymentMethod;
    private CartEntity cart;

    private static final int INITIAL_STOCK = 10;
    private static final int INITIAL_SOLD  = 5;
    private static final int ORDER_QTY     = 3;

    @BeforeEach
    void setUp() {
        // ── Role
        RoleEntity userRole = roleRepository.findByName("USER").orElseGet(() -> {
            RoleEntity r = new RoleEntity();
            r.setName("USER");
            return roleRepository.save(r);
        });

        // ── Account
        account = accountRepository.save(
                AccountEntity.builder()
                        .name("Test Buyer")
                        .email("buyer_order_test@example.com")
                        .passwordHash(passwordEncoder.encode("pass123"))
                        .role(userRole)
                        .status(1)
                        .build());

        // ── Product + Variant
        ProductEntity product = productRepository.save(
                ProductEntity.builder()
                        .name("Test Product")
                        .salePrice(new BigDecimal("99000"))
                        .status(1)
                        .build());

        variant = productVariantRepository.save(
                ProductVariantEntity.builder()
                        .sku("SKU-TEST-001")
                        .stock(INITIAL_STOCK)
                        .sold(INITIAL_SOLD)
                        .overridePrice(new BigDecimal("99000"))
                        .product(product)
                        .build());

        // ── Payment method (COD)
        paymentMethod = paymentMethodRepository.findAll().stream()
                .filter(pm -> "COD".equalsIgnoreCase(pm.getCode()))
                .findFirst()
                .orElseGet(() -> paymentMethodRepository.save(
                        PaymentMethodEntity.builder()
                                .code("COD")
                                .name("Thanh toán khi nhận hàng")
                                .build()));

        // ── Cart + CartItem
        cart = cartRepository.save(
                CartEntity.builder()
                        .account(account)
                        .build());

        CartItemEntity item = CartItemEntity.builder()
                .cart(cart)
                .variant(variant)
                .quantity(ORDER_QTY)
                .build();
        cart.getItems().add(cartItemRepository.save(item));
    }

    
    @Test
    @DisplayName("createOrder - Phải trừ tồn kho của variant đúng số lượng đặt")
    void createOrder_ShouldDecrementStock() {
        UserCreateOrderRequest request = buildRequest();

        orderService.createOrder(account.getId(), request);

        ProductVariantEntity updated = productVariantRepository.findById(variant.getId()).orElseThrow();
        assertThat(updated.getStock())
                .as("Tồn kho phải giảm đúng số lượng đặt")
                .isEqualTo(INITIAL_STOCK - ORDER_QTY);
    }

    
    @Test
    @DisplayName("createOrder - Phải cộng số lượng đã bán của variant đúng số lượng đặt")
    void createOrder_ShouldIncrementSold() {
        UserCreateOrderRequest request = buildRequest();

        orderService.createOrder(account.getId(), request);

        ProductVariantEntity updated = productVariantRepository.findById(variant.getId()).orElseThrow();
        assertThat(updated.getSold())
                .as("Số đã bán phải tăng đúng số lượng đặt")
                .isEqualTo(INITIAL_SOLD + ORDER_QTY);
    }

   
    @Test
    @DisplayName("createOrder - Giỏ hàng phải được làm trống sau khi đặt hàng thành công")
    void createOrder_ShouldClearCart() {
        UserCreateOrderRequest request = buildRequest();

        orderService.createOrder(account.getId(), request);

        CartEntity updatedCart = cartRepository.findByAccountId(account.getId()).orElseThrow();
        assertThat(updatedCart.getItems())
                .as("Giỏ hàng phải rỗng sau khi đặt hàng")
                .isEmpty();
    }


    @Test
    @DisplayName("createOrder - Đơn hàng mới phải có trạng thái PENDING và thông tin đúng")
    void createOrder_ShouldReturnCorrectOrderInfo() {
        UserCreateOrderRequest request = buildRequest();

        UserOrderResponse response = orderService.createOrder(account.getId(), request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getReceiver()).isEqualTo("Nguyen Van A");
        assertThat(response.getFinalAmount()).isGreaterThan(BigDecimal.ZERO);
    }


    @Test
    @DisplayName("createOrder - Phải ném exception khi tồn kho không đủ")
    void createOrder_ShouldThrow_WhenStockInsufficient() {
        // Đặt hàng nhiều hơn stock hiện có
        variant.setStock(2); // chỉ còn 2 nhưng cart đặt ORDER_QTY = 3
        productVariantRepository.save(variant);

        UserCreateOrderRequest request = buildRequest();

        assertThatThrownBy(() -> orderService.createOrder(account.getId(), request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("không đủ tồn kho");

        // Stock KHÔNG được thay đổi khi exception xảy ra (rollback)
        ProductVariantEntity unchanged = productVariantRepository.findById(variant.getId()).orElseThrow();
        assertThat(unchanged.getStock()).isEqualTo(2);
    }

   
    @Test
    @DisplayName("createOrder - Hai đơn liên tiếp phải trừ stock tích lũy đúng")
    void createOrder_TwoOrders_ShouldDecrementStockCumulatively() {
        // Đơn 1
        orderService.createOrder(account.getId(), buildRequest());

        // Setup lại cart cho đơn 2 (cart đã bị xóa sau đơn 1)
        CartEntity freshCart = cartRepository.findByAccountId(account.getId()).orElseThrow();
        assertThat(freshCart.getItems()).isEmpty();

        CartItemEntity item2 = CartItemEntity.builder()
                .cart(freshCart)
                .variant(productVariantRepository.findById(variant.getId()).orElseThrow())
                .quantity(2)
                .build();
        freshCart.getItems().add(cartItemRepository.save(item2));

        // Đơn 2
        orderService.createOrder(account.getId(), buildRequest());

        ProductVariantEntity updated = productVariantRepository.findById(variant.getId()).orElseThrow();
        assertThat(updated.getStock()).isEqualTo(INITIAL_STOCK - ORDER_QTY - 2); // 10-3-2 = 5
        assertThat(updated.getSold()).isEqualTo(INITIAL_SOLD + ORDER_QTY + 2);   // 5+3+2 = 10
    }


    private UserCreateOrderRequest buildRequest() {
        return UserCreateOrderRequest.builder()
                .paymentMethodId(paymentMethod.getId())
                .receiver("Nguyen Van A")
                .phone("0901234567")
                .location("123 Le Loi, Q1, HCM")
                .build();
    }
}
