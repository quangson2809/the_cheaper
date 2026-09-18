package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.dto.request.admin.AdminOrderStatusUpdateRequest;
import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.repository.*;
import com.example.the_cheaper.security.CustomUserDetailsService;
import com.example.the_cheaper.security.JwtProvider;
import com.example.the_cheaper.service.admin.AdminOrderService;
import com.example.the_cheaper.service.order.OrderService;
import com.example.the_cheaper.testconfig.MySqlIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class OrderApiAuthorizationIntegrationTest extends MySqlIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired AccountRepository accounts;
    @Autowired RoleRepository roles;
    @Autowired PermissionRepository permissions;
    @Autowired RolePermissionRepository assignments;
    @Autowired OrderRepository orders;
    @Autowired JwtProvider jwt;
    @Autowired CustomUserDetailsService detailsService;
    @Autowired AdminOrderService adminOrders;
    @Autowired OrderService userOrders;
    AccountEntity owner;
    AccountEntity staff;
    RoleEntity staffRole;

    @BeforeEach
    void prepare() {
        owner = account("phase4-owner", "PHASE4_OWNER");
        staff = account("phase4-staff", "PHASE4_STAFF");
        staffRole = staff.getAccountRoles().get(0).getRole();
    }

    @AfterEach
    void clearSecurityContext() { SecurityContextHolder.clearContext(); }

    @ParameterizedTest
    @CsvSource({
            "ORDER_CONFIRM,PENDING,PROCESSING,200", "ORDER_CONFIRM,SHIPPING,DELIVERED,200",
            "ORDER_CANCEL,PENDING,CANCELED,200", "ORDER_DELIVERY_UPDATE,PROCESSING,SHIPPING,200",
            "ORDER_READ,PENDING,PROCESSING,403", "ORDER_CONFIRM,PENDING,CANCELED,403",
            "ORDER_CANCEL,PENDING,PROCESSING,403", "ORDER_DELIVERY_UPDATE,SHIPPING,DELIVERED,403",
            "ORDER_PAYMENT_COLLECT,PENDING,PROCESSING,403", "ORDER_UPDATE,PENDING,PROCESSING,403"
    })
    void permissionsAreSeparated(String permission, OrderStatus initial, OrderStatus target, int expected) throws Exception {
        grant(staffRole, permission);
        OrderEntity order = order(initial);
        Long version = order.getVersion();
        mvc.perform(patch("/api/admin/orders/{id}/status", order.getId())
                .header("Authorization", bearer(staff)).contentType("application/json")
                .content("{\"status\":\"" + target + "\"}"))
                .andExpect(status().is(expected));
        orders.flush();
        assertThat(orders.findById(order.getId()).orElseThrow().getStatus())
                .isEqualTo(expected == 200 ? target : initial);
        if (expected == 403) assertThat(order.getVersion()).isEqualTo(version);
    }

    @Test
    void adminWithoutPermissionAssignmentsStillCannotMakeInvalidTransition() throws Exception {
        AccountEntity admin = account("phase4-admin", "ADMIN");
        assertThat(assignments.findAllByRoleId(admin.getAccountRoles().get(0).getRole().getId())).isEmpty();
        OrderEntity order = order(OrderStatus.PENDING);
        mvc.perform(patch("/api/admin/orders/{id}/status", order.getId())
                .header("Authorization", bearer(admin)).contentType("application/json")
                .content("{\"status\":\"DELIVERED\"}"))
                .andExpect(status().isBadRequest());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        mvc.perform(patch("/api/admin/orders/{id}/status", order.getId())
                .header("Authorization", bearer(admin)).contentType("application/json")
                .content("{\"status\":\"PROCESSING\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void customerCanOnlyReadAndCancelOwnOrders() throws Exception {
        grant(staffRole, "USER_ORDER_READ");
        grant(staffRole, "USER_ORDER_CANCEL");
        OrderEntity other = order(OrderStatus.PENDING);
        mvc.perform(get("/api/orders/{id}", other.getId()).header("Authorization", bearer(staff)))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/orders/{id}/cancel", other.getId()).header("Authorization", bearer(staff)))
                .andExpect(status().isNotFound());
        assertThat(other.getStatus()).isEqualTo(OrderStatus.PENDING);
        OrderEntity own = orders.saveAndFlush(OrderEntity.builder().account(staff).status(OrderStatus.PENDING)
                .paymentMethodCode("COD").finalAmount(BigDecimal.ZERO).build());
        mvc.perform(get("/api/orders/{id}", own.getId()).header("Authorization", bearer(staff)))
                .andExpect(status().isOk());
        mvc.perform(post("/api/orders/{id}/cancel", own.getId()).header("Authorization", bearer(staff)))
                .andExpect(status().isOk());
        assertThat(own.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void directServiceCallsCannotBypassPermissionsOrForgeOwnerId() {
        grant(staffRole, "USER_ORDER_READ");
        var details = detailsService.loadUserByUsername(staff.getEmail());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                details, null, details.getAuthorities()));
        OrderEntity order = order(OrderStatus.PENDING);
        assertThatThrownBy(() -> userOrders.getOrderDetail(order.getId(), owner.getId()))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> adminOrders.updateOrderStatus(order.getId(),
                new AdminOrderStatusUpdateRequest(OrderStatus.PROCESSING)))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> adminOrders.searchOrders(order.getId(), 1, 10))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void missingInvalidAndLockedAuthenticationReturn401() throws Exception {
        mvc.perform(get("/api/orders")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
        mvc.perform(get("/api/orders").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
        String token = bearer(staff);
        staff.deactivate();
        accounts.saveAndFlush(staff);
        mvc.perform(get("/api/orders").header("Authorization", token)).andExpect(status().isUnauthorized());
    }

    @Test
    void revokedPermissionTakesEffectWithTheSameToken() throws Exception {
        grant(staffRole, "ORDER_READ");
        String token = bearer(staff);
        mvc.perform(get("/api/admin/orders").header("Authorization", token)).andExpect(status().isOk());
        assignments.deleteByRoleIdAndPermissionId(staffRole.getId(), permissions.findByCode("ORDER_READ").orElseThrow().getId());
        assignments.flush();
        mvc.perform(get("/api/admin/orders").header("Authorization", token))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void invalidInputAndMissingOrderHaveDistinctResponses() throws Exception {
        grant(staffRole, "ORDER_READ");
        grant(staffRole, "ORDER_CONFIRM");
        mvc.perform(get("/api/admin/orders?page=0").header("Authorization", bearer(staff)))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/orders/9223372036854775807").header("Authorization", bearer(staff)))
                .andExpect(status().isNotFound());
        for (String body : new String[]{"{}", "{\"status\":\"NOT_A_STATUS\"}"}) {
            mvc.perform(patch("/api/admin/orders/1/status").header("Authorization", bearer(staff))
                    .contentType("application/json").content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        }
    }

    private AccountEntity account(String name, String roleName) {
        RoleEntity role = roles.findByName(roleName).orElseGet(() -> roles.save(RoleEntity.builder().name(roleName).build()));
        AccountEntity result = AccountEntity.builder().name(name).email(name + "@example.com")
                .passwordHash("test-only").status(1).build();
        result.addRole(role);
        return accounts.saveAndFlush(result);
    }
    private void grant(RoleEntity role, String permission) {
        assignments.saveAndFlush(RolePermissionEntity.builder().role(role)
                .permission(permissions.findByCode(permission).orElseThrow()).build());
    }
    private String bearer(AccountEntity account) { return "Bearer " + jwt.generateAccessToken(account); }
    private OrderEntity order(OrderStatus initial) {
        return orders.saveAndFlush(OrderEntity.builder().account(owner).status(initial).paymentMethodCode("COD")
                .paymentStatus(1).finalAmount(BigDecimal.ZERO).build());
    }
}
