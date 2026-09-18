package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Contract assertions document the baseline; they are not payment/inventory target requirements. */
class OrderAuthorizationRegressionTest extends RbacIntegrationSupport {
    @Autowired OrderRepository orders;
    @Autowired ProductRepository products;
    @Autowired ProductVariantRepository variants;
    @Autowired CartRepository carts;
    @Autowired PaymentMethodRepository methods;

    @ParameterizedTest @ValueSource(strings = {"DASHBOARD_READ", "ORDER_UPDATE", "ORDER_PAYMENT_COLLECT"})
    void unrelatedOrLegacyPermissionNeverGrantsOrderMutation(String code) throws Exception {
        var staff = account("staff", "REGRESSION_STAFF");
        grant(staff.getAccountRoles().get(0).getRole(), code);
        var order = order(staff, OrderStatus.PENDING);
        for (String target : new String[]{"PROCESSING", "CANCELED", "SHIPPING", "DELIVERED"}) {
            mvc.perform(patch("/api/admin/orders/{id}/status", order.getId()).header("Authorization", bearer(staff))
                    .contentType("application/json").content("{\"status\":\"" + target + "\"}"))
                    .andExpect(status().isForbidden());
        }
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test void adminStillObeysPersonalOwnershipBusinessStatesAndActiveAccountRule() throws Exception {
        var owner = account("owner", "USER");
        var admin = account("admin", "ADMIN");
        String token = bearer(admin);
        var other = order(owner, OrderStatus.PENDING);
        mvc.perform(get("/api/orders/{id}?accountId={owner}&role=ADMIN", other.getId(), owner.getId())
                .header("Authorization", token)).andExpect(status().isNotFound());
        mvc.perform(post("/api/orders/{id}/cancel", other.getId()).header("Authorization", token))
                .andExpect(status().isNotFound());
        mvc.perform(patch("/api/admin/orders/{id}/status", other.getId()).header("Authorization", token)
                .contentType("application/json").content("{\"status\":\"DELIVERED\"}"))
                .andExpect(status().isBadRequest());
        var shipping = order(admin, OrderStatus.SHIPPING);
        mvc.perform(patch("/api/admin/orders/{id}/status", shipping.getId()).header("Authorization", token)
                .contentType("application/json").content("{\"status\":\"DELIVERED\"}"))
                .andExpect(status().isBadRequest()); // unpaid COD, even for ADMIN
        admin.deactivate(); accounts.saveAndFlush(admin);
        mvc.perform(get("/api/admin/orders").header("Authorization", token)).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest @ValueSource(strings = {"COD", "MOMO"})
    void checkoutAndReadContractsKeepDocumentedBaseline(String paymentCode) throws Exception {
        var admin = account("admin", "ADMIN");
        var other = account("other", "USER");
        var product = products.saveAndFlush(ProductEntity.builder().name("Contract product").salePrice(new BigDecimal("100.00")).build());
        var variant = variants.saveAndFlush(ProductVariantEntity.builder().sku("SYNC-CONTRACT").product(product)
                .stock(10).sold(0).overridePrice(new BigDecimal("100.00")).build());
        var cart = CartEntity.builder().account(admin).build();
        cart.getItems().add(CartItemEntity.builder().cart(cart).variant(variant).quantity(2).build());
        carts.saveAndFlush(cart);
        var method = methods.saveAndFlush(PaymentMethodEntity.builder().code(paymentCode).name(paymentCode).status(1).build());
        String token = bearer(admin);
        mvc.perform(post("/api/orders").header("Authorization", token).contentType("application/json")
                .content("{\"paymentMethodId\":" + method.getId() + ",\"receiver\":\"Test\",\"phone\":\"0901234567\",\"location\":\"Test address\",\"accountId\":" + other.getId() + ",\"role\":\"ADMIN\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.finalAmount").value(200))
                .andExpect(jsonPath("$.data.items[0].price").value(100))
                .andExpect(jsonPath("$.data.items[0].unitPrice").value(200));
        var created = orders.findByAccountIdOrderByCreatedAtDesc(admin.getId(), org.springframework.data.domain.PageRequest.of(0, 10))
                .getContent().get(0);
        assertThat(created.getAccount().getId()).isEqualTo(admin.getId());
        assertThat(created.getPaymentStatus()).isEqualTo(paymentCode.equals("COD") ? 0 : 1);
        mvc.perform(get("/api/orders?page=1").header("Authorization", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.number").value(0));
        mvc.perform(get("/api/admin/orders/{id}", created.getId()).header("Authorization", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.finalTotal").value(200))
                .andExpect(jsonPath("$.data.finalAmount").doesNotExist());
        if (paymentCode.equals("COD")) {
            mvc.perform(post("/api/orders/{id}/cancel", created.getId()).header("Authorization", token)).andExpect(status().isOk());
        }
        // Existing limitation: cancellation has no stock/sold compensation yet.
        assertThat(variant.getStock()).isEqualTo(8);
        assertThat(variant.getSold()).isEqualTo(2);
    }

    private OrderEntity order(AccountEntity account, OrderStatus status) {
        return orders.saveAndFlush(OrderEntity.builder().account(account).status(status).paymentMethodCode("COD")
                .paymentStatus(0).finalAmount(BigDecimal.ZERO).build());
    }
}
