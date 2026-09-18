package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.TheCheaperApplication;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.security.JwtProvider;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.mysql.MySQLContainer;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Full application close/start, same disposable MySQL, production seeders enabled. */
@Tag("integration")
class Phase5RestartIntegrationTest {
    @Test void freshLegacyAndRepeatedStartupPreserveDataAndRevocation() throws Exception {
        try (var mysql = new MySQLContainer("mysql:8.4").withDatabaseName("phase5_restart")) {
            mysql.start();
            String token;
            long userRole, accountId, confirm, targetOrder;
            Map<String, List<Map<String, Object>>> stable;
            try (var app = start(mysql, "create")) {
                var db = app.getBean(JdbcTemplate.class);
                userRole = id(db, "select id from roles where name = 'USER'");
                accountId = id(db, "select id from accounts where email = 'an.nguyen@gmail.com'");
                confirm = id(db, "select id from permissions where code = 'ORDER_CONFIRM'");
                targetOrder = id(db, "select id from orders where status = 'PENDING' limit 1");
                assertThat(id(db, "select count(*) from accounts")).isEqualTo(10);
                assertThat(id(db, "select count(*) from orders")).isEqualTo(10);
                assertThat(id(db, "select count(*) from role_permissions rp join permissions p on p.id=rp.permission_id where rp.role_id="
                        + userRole + " and p.code like 'USER_ORDER_%'")).isEqualTo(3);
                assertThat(id(db, "select count(*) from role_permissions rp join permissions p on p.id=rp.permission_id where rp.role_id="
                        + userRole + " and p.code like 'ORDER_%'")).isZero();
                token = "Bearer " + app.getBean(JwtProvider.class).generateAccessToken(
                        app.getBean(AccountRepository.class).findByEmail("an.nguyen@gmail.com").orElseThrow());
                var mvc = mvc(app);
                mvc.perform(get("/api/orders").header("Authorization", token)).andExpect(status().isOk());
                assertCatalog(db);
                mvc.perform(get("/api/admin/stats?year=2026").header("Authorization", token)).andExpect(status().isForbidden());
                // Legacy database: nullable versions + forbidden USER grants + missing catalog entry.
                db.execute("alter table product_variants modify version bigint null");
                db.update("update product_variants set version=null");
                db.update("update orders set version=null");
                long legacy = id(db, "select id from permissions where code='ORDER_UPDATE'");
                db.update("insert into role_permissions(role_id,permission_id) values (?,?)", userRole, legacy);
                db.update("insert into roles(name) values ('P5_STAFF')");
                long staffRole = id(db, "select id from roles where name='P5_STAFF'");
                db.update("insert into account_roles(account_id,role_id) values (?,?)", accountId, staffRole);
                db.update("insert into role_permissions(role_id,permission_id) values (?,?)", staffRole, confirm);
                mvc.perform(patch("/api/admin/orders/{id}/status", targetOrder).header("Authorization", token)
                        .contentType("application/json").content("{\"status\":\"SHIPPING\"}"))
                        .andExpect(status().isForbidden());
                db.update("delete from role_permissions where role_id=? and permission_id=?", staffRole, confirm);
                long collect = id(db, "select id from permissions where code='ORDER_PAYMENT_COLLECT'");
                db.update("delete from role_permissions where permission_id=?", collect);
                db.update("delete from permissions where id=?", collect);
                for (String code : List.of("DASHBOARD_READ", "ROLE_PERMISSION_READ", "ROLE_PERMISSION_UPDATE",
                        "ROLE_PERMISSION_GRANT", "ROLE_PERMISSION_REVOKE", "ACCOUNT_ROLE_READ", "ACCOUNT_ROLE_UPDATE", "ACCOUNT_STATUS_UPDATE")) {
                    long permissionId = id(db, "select id from permissions where code='" + code + "'");
                    db.update("insert into role_permissions(role_id,permission_id) values (?,?)", staffRole, permissionId);
                    db.update("delete from role_permissions where role_id=? and permission_id=?", staffRole, permissionId);
                }
                db.update("insert into roles(name) values ('SYNC_LEGACY')");
                long legacyRole = id(db, "select id from roles where name='SYNC_LEGACY'");
                db.update("insert into account_roles(account_id,role_id) values (?,?)", accountId, legacyRole);
                for (String code : List.of("ROLE_ASSIGN_PERMISSION", "ACCOUNT_ASSIGN_ROLE")) {
                    db.update("insert into role_permissions(role_id,permission_id) values (?,?)", legacyRole,
                            id(db, "select id from permissions where code='" + code + "'"));
                }
                // Exercise an existing installation missing a newly canonical code, too.
                db.update("delete from permissions where code='DASHBOARD_READ'");
                // Business state excludes versions, which are intentionally migrated from null to 0.
                stable = businessSnapshot(db);
            }
            for (int restart = 0; restart < 2; restart++) {
                try (var app = start(mysql, "update")) {
                    var db = app.getBean(JdbcTemplate.class);
                    assertCatalog(db);
                    assertThat(businessSnapshot(db)).isEqualTo(stable);
                    assertThat(id(db, "select count(*) from orders where version is null")).isZero();
                    assertThat(id(db, "select count(*) from product_variants where version is null")).isZero();
                    assertThat(id(db, "select count(*) from permissions where code='ORDER_PAYMENT_COLLECT'")).isEqualTo(1);
                    assertThat(id(db, "select count(*) from role_permissions rp join permissions p on p.id=rp.permission_id where rp.role_id="
                            + userRole + " and p.code like 'ORDER_%'")).isZero();
                    assertThat(id(db, "select count(*) from role_permissions rp join roles r on r.id=rp.role_id where r.name='P5_STAFF'")).isZero();
                    assertThat(id(db, "select count(*) from role_permissions rp join roles r on r.id=rp.role_id where r.name='SYNC_LEGACY'")).isEqualTo(2);
                    var mvc = mvc(app);
                    mvc.perform(get("/api/admin/stats?year=2026").header("Authorization", token)).andExpect(status().isForbidden());
                    mvc.perform(get("/api/admin/roles/{id}/permissions", userRole).header("Authorization", token)).andExpect(status().isForbidden());
                    mvc.perform(put("/api/admin/accounts/{id}/role", accountId).header("Authorization", token)
                            .contentType("application/json").content("{\"roleId\":" + userRole + "}"))
                            .andExpect(status().isForbidden());
                    mvc.perform(get("/api/orders").header("Authorization", token)).andExpect(status().isOk());
                    mvc.perform(patch("/api/admin/orders/{id}/status", targetOrder).header("Authorization", token)
                            .contentType("application/json").content("{\"status\":\"PROCESSING\"}"))
                            .andExpect(status().isForbidden());
                    assertThat(businessSnapshot(db)).isEqualTo(stable);
                }
            }
        }
    }
    private void assertCatalog(JdbcTemplate db) {
        assertThat(db.queryForList("select code from permissions", String.class))
                .containsAll(com.example.the_cheaper.config.PermissionCatalog.CODES);
    }
    private ConfigurableApplicationContext start(MySQLContainer mysql, String ddl) {
        // Explicit test-only config location prevents reading developer DB/mail credentials.
        // Profile is deliberately NOT 'test': DataSeeder must participate in real startup.
        return new SpringApplicationBuilder(TheCheaperApplication.class).run(
                "--spring.config.location=classpath:application-test.properties", "--spring.profiles.active=phase5-restart",
                "--spring.datasource.url=" + mysql.getJdbcUrl(), "--spring.datasource.username=" + mysql.getUsername(),
                "--spring.datasource.password=" + mysql.getPassword(), "--spring.jpa.hibernate.ddl-auto=" + ddl,
                "--server.port=0", "--logging.level.root=WARN");
    }
    private MockMvc mvc(ConfigurableApplicationContext app) {
        return MockMvcBuilders.webAppContextSetup((WebApplicationContext) app).apply(springSecurity()).build();
    }
    private long id(JdbcTemplate db, String sql) { return db.queryForObject(sql, Long.class); }
    private Map<String, List<Map<String, Object>>> businessSnapshot(JdbcTemplate db) {
        return Map.of("orders", db.queryForList("select id,account_id,status,final_amount,payment_status,payment_method_code from orders order by id"),
                "stock", db.queryForList("select id,sku,stock,sold,overite_sale_price from product_variants order by id"),
                "payments", db.queryForList("select * from payments order by id"),
                "items", db.queryForList("select * from order_items order by id"),
                "accounts", db.queryForList("select id,email,status from accounts order by id"));
    }
}
