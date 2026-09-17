package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.repository.*;
import com.example.the_cheaper.security.JwtProvider;
import com.example.the_cheaper.mapper.admin.AdminOrderMapper;
import com.example.the_cheaper.testconfig.MySqlIntegrationTest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** No test transaction: requests commit/rollback normally; assertions re-read MySQL using JDBC. */
@AutoConfigureMockMvc
class Phase5OrderSecurityIntegrationTest extends MySqlIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate db;
    @Autowired PlatformTransactionManager transactions;
    @Autowired AccountRepository accounts;
    @Autowired RoleRepository roles;
    @Autowired PermissionRepository permissions;
    @Autowired RolePermissionRepository grants;
    @Autowired ProductRepository products;
    @Autowired ProductVariantRepository variants;
    @Autowired OrderRepository orders;
    @Autowired PaymentRepository payments;
    @Autowired CartRepository carts;
    @Autowired PaymentMethodRepository methods;
    @Autowired JwtProvider jwt;
    @MockitoSpyBean AdminOrderMapper mapper;
    @Value("${jwt.secret}") String signingKey;
    TransactionTemplate tx;
    AccountEntity customer, staff;
    RoleEntity customerRole, staffRole;
    long ownOrder, otherOrder, variantId, methodId;
    String token;

    @BeforeEach void fixture() {
        tx = new TransactionTemplate(transactions);
        tx.executeWithoutResult(s -> {
            customerRole = roles.save(RoleEntity.builder().name("P5_CUSTOMER").build());
            staffRole = roles.save(RoleEntity.builder().name("P5_STAFF").build());
            customer = account("p5-customer", customerRole);
            staff = account("p5-staff", staffRole);
            var product = products.save(ProductEntity.builder().name("Phase 5 fixture").salePrice(new BigDecimal("100.00")).build());
            var variant = variants.save(ProductVariantEntity.builder().sku("P5-SKU").product(product)
                    .stock(10).sold(2).overridePrice(new BigDecimal("100.00")).build());
            variantId = variant.getId();
            ownOrder = order(staff, variant, OrderStatus.PENDING);
            otherOrder = order(customer, variant, OrderStatus.PENDING);
            var cart = CartEntity.builder().account(staff).build();
            cart.getItems().add(CartItemEntity.builder().cart(cart).variant(variant).quantity(2).build());
            carts.save(cart);
            methodId = methods.save(PaymentMethodEntity.builder().code("P5-COD").name("Fixture").status(1).build()).getId();
        });
        // COD is a snapshot code, use a dedicated fixture row with the real COD code.
        db.update("update payment_methods set code = 'COD' where id = ?", methodId);
        token = "Bearer " + jwt.generateAccessToken(staff);
    }

    @AfterEach void cleanup() {
        // This context owns an isolated MySQL container; only fixture records exist here.
        for (String table : List.of("payments", "order_items", "orders", "cart_items", "carts",
                "variant_option_values", "product_variants", "products", "payment_methods",
                "account_roles", "accounts", "role_permissions", "roles")) db.update("delete from " + table);
    }

    static Stream<Arguments> staffMatrix() {
        return Stream.of("NONE", "ORDER_READ", "ORDER_CONFIRM", "ORDER_CANCEL", "ORDER_DELIVERY_UPDATE",
                "ORDER_PAYMENT_COLLECT", "ORDER_UPDATE").flatMap(permission -> Stream.of(
                Arguments.of(permission, "LIST", "PENDING", permission.equals("ORDER_READ") ? 200 : 403),
                Arguments.of(permission, "DETAIL", "PENDING", permission.equals("ORDER_READ") ? 200 : 403),
                Arguments.of(permission, "PROCESSING", "PENDING", permission.equals("ORDER_CONFIRM") ? 200 : 403),
                Arguments.of(permission, "CANCELED", "PENDING", permission.equals("ORDER_CANCEL") ? 200 : 403),
                Arguments.of(permission, "SHIPPING", "PROCESSING", permission.equals("ORDER_DELIVERY_UPDATE") ? 200 : 403),
                Arguments.of(permission, "DELIVERED", "SHIPPING", permission.equals("ORDER_CONFIRM") ? 200 : 403)));
    }

    @ParameterizedTest(name = "{0} -> {1}: HTTP {3}") @MethodSource("staffMatrix")
    void staffPermissionMatrix(String permission, String operation, String initial, int expected) throws Exception {
        grant(staffRole, permission);
        db.update("update orders set status = ?, payment_status = 1 where id = ?", initial, otherOrder);
        var before = snapshot();
        var request = switch (operation) {
            case "LIST" -> get("/api/admin/orders");
            case "DETAIL" -> get("/api/admin/orders/{id}", otherOrder);
            default -> change(otherOrder, operation);
        };
        mvc.perform(request.header("Authorization", token)).andExpect(status().is(expected));
        if (expected == 403 || operation.equals("LIST") || operation.equals("DETAIL")) {
            assertThat(snapshot()).isEqualTo(before);
        } else {
            assertThat(orderRow(otherOrder).get("status")).isEqualTo(operation);
            assertThat(((Number) orderRow(otherOrder).get("version")).longValue()).isEqualTo(1);
            assertThat(snapshot().get("stock")).isEqualTo(before.get("stock"));
            assertThat(snapshot().get("payments")).isEqualTo(before.get("payments"));
        }
    }

    static Stream<Arguments> customerMatrix() {
        return Stream.of("NONE", "USER_ORDER_READ", "USER_ORDER_CREATE", "USER_ORDER_CANCEL")
                .flatMap(p -> Stream.of("LIST", "DETAIL", "CREATE", "CANCEL").map(op -> {
                    String required = switch (op) { case "CREATE" -> "USER_ORDER_CREATE";
                        case "CANCEL" -> "USER_ORDER_CANCEL"; default -> "USER_ORDER_READ"; };
                    return Arguments.of(p, op, p.equals(required) ? (op.equals("CREATE") ? 201 : 200) : 403);
                }));
    }

    @ParameterizedTest(name = "{0} -> personal {1}: HTTP {2}") @MethodSource("customerMatrix")
    void customerPermissionMatrix(String permission, String operation, int expected) throws Exception {
        grant(staffRole, permission);
        var before = snapshot();
        var request = switch (operation) {
            case "LIST" -> get("/api/orders");
            case "DETAIL" -> get("/api/orders/{id}", ownOrder);
            case "CANCEL" -> post("/api/orders/{id}/cancel", ownOrder);
            default -> post("/api/orders").contentType("application/json").content(createBody());
        };
        mvc.perform(request.header("Authorization", token)).andExpect(status().is(expected));
        if (expected == 403 || operation.equals("LIST") || operation.equals("DETAIL")) assertThat(snapshot()).isEqualTo(before);
        else if (operation.equals("CANCEL")) {
            assertThat(orderRow(ownOrder).get("status")).isEqualTo("CANCELED");
            assertThat(snapshot().get("stock")).isEqualTo(before.get("stock"));
            assertThat(snapshot().get("payments")).isEqualTo(before.get("payments"));
        } else {
            assertThat(db.queryForObject("select count(*) from orders", Integer.class)).isEqualTo(3);
            assertThat(db.queryForObject("select stock from product_variants where id = ?", Integer.class, variantId)).isEqualTo(8);
            assertThat(db.queryForObject("select sold from product_variants where id = ?", Integer.class, variantId)).isEqualTo(4);
            assertThat(db.queryForObject("select count(*) from cart_items", Integer.class)).isZero();
            var created = db.queryForMap("select account_id, payment_status, final_amount from orders order by id desc limit 1");
            assertThat(((Number) created.get("account_id")).longValue()).isEqualTo(staff.getId());
            assertThat(created.get("payment_status")).isEqualTo(0);
            assertThat((BigDecimal) created.get("final_amount")).isEqualByComparingTo("200.00");
        }
    }

    @ParameterizedTest @ValueSource(strings = {"missing", "malformed", "expired", "wrong-signature", "locked"})
    void rejectedAuthenticationCannotMutateAnything(String kind) throws Exception {
        grant(staffRole, "ORDER_CONFIRM");
        String header = token;
        if (kind.equals("malformed")) header = "Bearer not-a-jwt";
        if (kind.equals("expired") || kind.equals("wrong-signature")) {
            var key = Keys.hmacShaKeyFor(kind.equals("expired") ? Decoders.BASE64.decode(signingKey) : new byte[32]);
            header = "Bearer " + Jwts.builder().subject(staff.getEmail()).claim("userId", staff.getId())
                    .issuedAt(new Date(System.currentTimeMillis() - 120000))
                    .expiration(new Date(System.currentTimeMillis() + (kind.equals("expired") ? -60000 : 60000)))
                    .signWith(key).compact();
        }
        if (kind.equals("locked")) db.update("update accounts set status = 0 where id = ?", staff.getId());
        var before = snapshot();
        var request = change(otherOrder, "PROCESSING");
        if (!kind.equals("missing")) request.header("Authorization", header);
        mvc.perform(request).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
        assertThat(snapshot()).isEqualTo(before);
    }

    @ParameterizedTest @ValueSource(strings = {"USER", "MULTI_ROLE", "ADMIN"})
    void personalScopeSurvivesExtraRolesAndForgedAccountId(String kind) throws Exception {
        grant(staffRole, "USER_ORDER_READ"); grant(staffRole, "USER_ORDER_CANCEL");
        if (!kind.equals("USER")) tx.executeWithoutResult(s -> {
            var role = roles.save(RoleEntity.builder().name(kind.equals("ADMIN") ? "ADMIN" : "P5_SECOND").build());
            var account = accounts.findById(staff.getId()).orElseThrow(); account.addRole(role); accounts.save(account);
            if (kind.equals("MULTI_ROLE")) grant(role, "ORDER_READ");
        });
        var before = snapshot();
        mvc.perform(get("/api/orders/{id}", otherOrder).param("accountId", customer.getId().toString()).header("Authorization", token))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/orders/{id}/cancel", otherOrder).header("Authorization", token))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/orders").param("accountId", customer.getId().toString()).header("Authorization", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(ownOrder));
        assertThat(snapshot()).isEqualTo(before);
        if (!kind.equals("USER")) mvc.perform(get("/api/admin/orders/{id}", otherOrder).header("Authorization", token)).andExpect(status().isOk());
    }

    @Test void grantAndRevokeAcrossTwoRolesTakeEffectWithOriginalToken() throws Exception {
        mvc.perform(change(otherOrder, "PROCESSING").header("Authorization", token)).andExpect(status().isForbidden());
        grant(staffRole, "ORDER_CONFIRM");
        RoleEntity second = tx.execute(s -> {
            var role = roles.save(RoleEntity.builder().name("P5_SECOND").build());
            var account = accounts.findById(staff.getId()).orElseThrow(); account.addRole(role); accounts.save(account);
            grant(role, "ORDER_CONFIRM"); grant(role, "ORDER_CANCEL"); return role;
        });
        revoke(staffRole, "ORDER_CONFIRM");
        mvc.perform(change(otherOrder, "PROCESSING").header("Authorization", token)).andExpect(status().isOk());
        revoke(second, "ORDER_CONFIRM");
        var before = snapshot();
        mvc.perform(change(ownOrder, "PROCESSING").header("Authorization", token)).andExpect(status().isForbidden());
        assertThat(snapshot()).isEqualTo(before);
        mvc.perform(change(otherOrder, "CANCELED").header("Authorization", token)).andExpect(status().isOk());
        assertThat(orderRow(otherOrder).get("status")).isEqualTo("CANCELED");
    }

    @Test void confirmPermissionCannotCollectPaymentByInjectingFields() throws Exception {
        grant(staffRole, "ORDER_CONFIRM");
        var before = snapshot();
        mvc.perform(patch("/api/admin/orders/{id}/status", otherOrder).header("Authorization", token)
                .contentType("application/json").content("{\"status\":\"PROCESSING\",\"paymentStatus\":1,\"paymentMethodCode\":\"MOMO\"}"))
                .andExpect(status().isOk());
        assertThat(orderRow(otherOrder).get("payment_status")).isEqualTo(0);
        assertThat(orderRow(otherOrder).get("payment_method_code")).isEqualTo("COD");
        assertThat(snapshot().get("payments")).isEqualTo(before.get("payments"));
        // No collection API exists yet: this proves field injection is denied, not COD acceptance.
    }

    @ParameterizedTest @ValueSource(strings = {"SHIPPING", "DELIVERED", "CANCELED", "REFUNDED"})
    void invalidCancellationRollsBackAllData(String initial) throws Exception {
        grant(staffRole, "USER_ORDER_CANCEL");
        db.update("update orders set status = ? where id = ?", initial, ownOrder);
        var before = snapshot();
        mvc.perform(post("/api/orders/{id}/cancel", ownOrder).header("Authorization", token)).andExpect(status().isBadRequest());
        assertThat(snapshot()).isEqualTo(before);
    }

    @Test void concurrentConflictingHttpRequestsHaveOneWinnerAndOne409() throws Exception {
        grant(staffRole, "ORDER_CONFIRM"); grant(staffRole, "ORDER_CANCEL");
        var before = snapshot();
        var barrier = new CyclicBarrier(2);
        // Synchronize after both real transactions load/mutate version 0, before either commits.
        // Only DTO mapping is spied; authentication, authorization, repositories and transactions are real.
        doAnswer(invocation -> {
            barrier.await(15, TimeUnit.SECONDS);
            return invocation.callRealMethod();
        }).when(mapper).toOverviewResponse(any(OrderEntity.class));
        var pool = Executors.newFixedThreadPool(2);
        try {
            var confirm = pool.submit(() -> mvc.perform(change(otherOrder, "PROCESSING").header("Authorization", token)).andReturn());
            var cancel = pool.submit(() -> mvc.perform(change(otherOrder, "CANCELED").header("Authorization", token)).andReturn());
            var a = confirm.get(30, TimeUnit.SECONDS).getResponse();
            var b = cancel.get(30, TimeUnit.SECONDS).getResponse();
            assertThat(List.of(a.getStatus(), b.getStatus())).containsExactlyInAnyOrder(200, 409);
            assertThat(orderRow(otherOrder).get("status")).isEqualTo(a.getStatus() == 200 ? "PROCESSING" : "CANCELED");
            assertThat(((Number) orderRow(otherOrder).get("version")).longValue()).isEqualTo(1);
            assertThat((a.getStatus() == 409 ? a : b).getContentAsString()).contains("409");
            assertThat(snapshot().get("stock")).isEqualTo(before.get("stock"));
            assertThat(snapshot().get("payments")).isEqualTo(before.get("payments"));
        } finally { pool.shutdownNow(); assertThat(pool.awaitTermination(10, TimeUnit.SECONDS)).isTrue(); }
    }

    private AccountEntity account(String name, RoleEntity role) {
        var result = AccountEntity.builder().name(name).email(name + "@example.com").passwordHash("test").status(1).build();
        result.addRole(role); return accounts.save(result);
    }
    private long order(AccountEntity account, ProductVariantEntity variant, OrderStatus status) {
        var result = OrderEntity.builder().account(account).status(status).paymentMethodCode("COD")
                .finalAmount(new BigDecimal("200.00")).paymentStatus(0).build();
        result.getItems().add(OrderItemEntity.builder().order(result).variant(variant).quantity(2).price(new BigDecimal("100.00")).build());
        orders.save(result);
        payments.save(PaymentEntity.builder().order(result).method("COD").status("PENDING").amount(new BigDecimal("200.00")).build());
        return result.getId();
    }
    private void grant(RoleEntity role, String code) {
        if (code.equals("NONE")) return;
        tx.executeWithoutResult(s -> grants.saveAndFlush(RolePermissionEntity.builder().role(role)
                .permission(permissions.findByCode(code).orElseThrow()).build()));
    }
    private void revoke(RoleEntity role, String code) {
        tx.executeWithoutResult(s -> grants.deleteByRoleIdAndPermissionId(role.getId(), permissions.findByCode(code).orElseThrow().getId()));
    }
    private MockHttpServletRequestBuilder change(long id, String target) {
        return patch("/api/admin/orders/{id}/status", id).contentType("application/json").content("{\"status\":\"" + target + "\"}");
    }
    private String createBody() {
        return "{\"paymentMethodId\":" + methodId + ",\"receiver\":\"Fixture\",\"phone\":\"0901234567\",\"location\":\"Test address\",\"accountId\":" + customer.getId() + "}";
    }
    private Map<String, Object> orderRow(long id) { return db.queryForMap("select * from orders where id = ?", id); }
    private Map<String, Object> snapshot() {
        return Map.of("orders", db.queryForList("select * from orders order by id"),
                "items", db.queryForList("select * from order_items order by id"),
                "stock", db.queryForList("select * from product_variants order by id"),
                "payments", db.queryForList("select * from payments order by id"),
                "cart", db.queryForList("select * from cart_items order by id"));
    }
}
