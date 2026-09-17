package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.repository.*;
import com.example.the_cheaper.testconfig.MySqlIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/** Runs the delivered collection over a real HTTP socket, with fresh deterministic fixtures. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Phase5PostmanIntegrationTest extends MySqlIntegrationTest {
    @LocalServerPort int port;
    @Autowired AccountRepository accounts;
    @Autowired RoleRepository roles;
    @Autowired RolePermissionRepository grants;
    @Autowired PermissionRepository permissions;
    @Autowired ProductRepository products;
    @Autowired ProductVariantRepository variants;
    @Autowired PaymentMethodRepository methods;
    @Autowired OrderRepository orders;
    @Autowired CartRepository carts;
    @Autowired PasswordEncoder encoder;
    @Autowired PlatformTransactionManager transactions;
    @Autowired JdbcTemplate db;

    @Test void deliveredPostmanCollectionPassesAgainstRealServer() throws Exception {
        Path runner = Path.of(".phase5-tools/node_modules/newman/bin/newman.js");
        assertThat(runner).as("Install pinned runner: npm install --prefix .phase5-tools newman@6.2.1").exists();
        var values = new LinkedHashMap<String, Object>();
        values.put("baseUrl", "http://127.0.0.1:" + port);
        values.put("password", "Phase5-test-only-123!");
        new TransactionTemplate(transactions).executeWithoutResult(s -> {
            var product = products.save(ProductEntity.builder().name("Postman fixture").salePrice(new BigDecimal("100.00")).build());
            var variant = variants.save(ProductVariantEntity.builder().sku("P5-POSTMAN").product(product).stock(20).sold(0).build());
            values.put("variantId", variant.getId());
            values.put("paymentMethodId", methods.save(PaymentMethodEntity.builder().code("COD").name("Cash").status(1).build()).getId());
            for (String actor : List.of("userA", "userB", "admin", "reader", "confirm", "cancel", "delivery", "collect", "none")) {
                var role = roles.save(RoleEntity.builder().name(actor.equals("admin") ? "ADMIN" : "POSTMAN_" + actor).build());
                List<String> codes = switch (actor) {
                    case "userA", "userB" -> List.of("USER_ORDER_CREATE", "USER_ORDER_READ", "USER_ORDER_CANCEL");
                    case "reader" -> List.of("ORDER_READ");
                    case "confirm" -> List.of("ORDER_CONFIRM");
                    case "cancel" -> List.of("ORDER_CANCEL");
                    case "delivery" -> List.of("ORDER_DELIVERY_UPDATE");
                    case "collect" -> List.of("ORDER_PAYMENT_COLLECT");
                    default -> List.of();
                };
                for (String code : codes) grants.save(RolePermissionEntity.builder().role(role)
                        .permission(permissions.findByCode(code).orElseThrow()).build());
                var account = AccountEntity.builder().name(actor).email(actor + "@phase5.example.com").status(1)
                        .passwordHash(encoder.encode(values.get("password").toString())).build();
                account.addRole(role); accounts.save(account);
                values.put(actor + "Email", account.getEmail());
                if (actor.startsWith("user")) carts.save(CartEntity.builder().account(account).build());
                if (actor.equals("userB")) {
                    var paid = orders.save(OrderEntity.builder().account(account).status(OrderStatus.SHIPPING)
                            .paymentMethodCode("COD").paymentStatus(1).finalAmount(BigDecimal.ZERO).build());
                    values.put("paidShippingOrderId", paid.getId());
                }
            }
        });
        Path evidence = Path.of("build/phase5"); Files.createDirectories(evidence);
        Path environment = evidence.resolve("postman.environment.json");
        var json = new ObjectMapper();
        json.writeValue(environment.toFile(), Map.of("name", "Isolated Phase 5 fixtures", "values", values.entrySet().stream()
                .map(e -> Map.of("key", e.getKey(), "value", e.getValue().toString(), "enabled", true)).toList()));
        Process process = new ProcessBuilder("node", runner.toString(), "run",
                "docs/postman/TheCheaper-Order-RBAC.postman_collection.json", "-e", environment.toString(),
                "--reporters", "cli,json,junit", "--reporter-json-export", evidence.resolve("newman.json").toString(),
                "--reporter-junit-export", evidence.resolve("newman.xml").toString(), "--timeout-request", "15000")
                .redirectErrorStream(true).redirectOutput(evidence.resolve("newman.log").toFile()).start();
        try {
            assertThat(process.waitFor(180, TimeUnit.SECONDS)).as("Newman timeout").isTrue();
            assertThat(process.exitValue()).withFailMessage("Newman failed: %s", Files.readString(evidence.resolve("newman.log"))).isZero();
            assertThat(db.queryForObject("select stock from product_variants where sku='P5-POSTMAN'", Integer.class)).isEqualTo(16);
            assertThat(db.queryForObject("select sold from product_variants where sku='P5-POSTMAN'", Integer.class)).isEqualTo(4);
            assertThat(db.queryForObject("select count(*) from orders", Integer.class)).isEqualTo(3);
            assertThat(db.queryForObject("select count(*) from orders where status='CANCELED'", Integer.class)).isEqualTo(1);
            assertThat(db.queryForObject("select count(*) from orders where status='SHIPPING'", Integer.class)).isEqualTo(1);
            assertThat(db.queryForObject("select count(*) from orders where status='DELIVERED'", Integer.class)).isEqualTo(1);
            assertThat(db.queryForObject("select count(*) from payments", Integer.class)).isZero();
        } finally { process.destroyForcibly(); }
    }
}
