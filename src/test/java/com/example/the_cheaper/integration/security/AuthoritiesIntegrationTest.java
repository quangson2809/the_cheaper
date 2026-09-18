package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.config.PermissionCatalog;
import com.example.the_cheaper.entity.RoleEntity;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthoritiesIntegrationTest extends RbacIntegrationSupport {
    @Test void multiRoleUnionIncludesRolePermissionCodesAndTracksRevocationWithSameJwt() throws Exception {
        var staff = account("staff", "STAFF");
        var first = staff.getAccountRoles().get(0).getRole();
        var second = roles.saveAndFlush(RoleEntity.builder().name("ORDER_MANAGER").build());
        staff.addRole(second); accounts.saveAndFlush(staff);
        grant(first, "ORDER_READ"); grant(second, "ORDER_READ");
        grant(second, "ORDER_CONFIRM"); grant(first, "ROLE_PERMISSION_READ");
        String token = bearer(staff);
        mvc.perform(get("/api/auth/me/authorities")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth/me/authorities?accountId=999&role=ADMIN").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roles", contains("ORDER_MANAGER", "STAFF")))
                .andExpect(jsonPath("$.data.permissions", contains("ORDER_CONFIRM", "ORDER_READ", "ROLE_PERMISSION_READ")));
        var read = permissions.findByCode("ORDER_READ").orElseThrow();
        grants.deleteByRoleIdAndPermissionId(first.getId(), read.getId()); grants.flush();
        mvc.perform(get("/api/auth/me/authorities").header("Authorization", token))
                .andExpect(jsonPath("$.data.permissions", hasItem("ORDER_READ")));
        grants.deleteByRoleIdAndPermissionId(second.getId(), read.getId()); grants.flush();
        mvc.perform(get("/api/auth/me/authorities").header("Authorization", token))
                .andExpect(jsonPath("$.data.permissions", not(hasItem("ORDER_READ"))));
        staff.deactivate(); accounts.saveAndFlush(staff);
        mvc.perform(get("/api/auth/me/authorities").header("Authorization", token)).andExpect(status().isUnauthorized());
    }

    @Test void adminReceivesEntireCatalogWithoutAssignments() throws Exception {
        var admin = account("admin", "ADMIN");
        mvc.perform(get("/api/auth/me/authorities").header("Authorization", bearer(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.roles", contains("ADMIN")))
                .andExpect(jsonPath("$.data.permissions", containsInAnyOrder(PermissionCatalog.CODES.toArray())));
    }
}
