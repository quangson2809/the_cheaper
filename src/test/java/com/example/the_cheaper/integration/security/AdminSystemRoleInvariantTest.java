package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.service.authorization.AuthorizationQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminSystemRoleInvariantTest extends RbacIntegrationSupport {
    @Autowired AuthorizationQueryService authorization;

    @ParameterizedTest @ValueSource(strings = {"ADMIN", "USER"})
    void systemRoleCannotBeRenamedOrDeleted(String roleName) throws Exception {
        var admin = account("admin", "ADMIN");
        var role = roles.findByName(roleName).orElseGet(() -> roles.save(
                com.example.the_cheaper.entity.RoleEntity.builder().name(roleName).build()));
        String token = bearer(admin);
        mvc.perform(put("/api/admin/roles/{id}", role.getId()).header("Authorization", token)
                .contentType("application/json").content("{\"name\":\"RENAMED\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(delete("/api/admin/roles/{id}", role.getId()).header("Authorization", token))
                .andExpect(status().isBadRequest());
        assertThat(roles.findById(role.getId()).orElseThrow().getName()).isEqualTo(roleName);
        assertThat(authorization.findAuthorities(admin.getId())).contains("ROLE_ADMIN", "DASHBOARD_READ");
        mvc.perform(get("/api/admin/roles").header("Authorization", token)).andExpect(status().isOk());
        mvc.perform(get("/api/admin/permissions").header("Authorization", token)).andExpect(status().isOk());
    }

    @Test void lastActiveAdminCannotBeDisabledOrDeletedButAnotherAdminAllowsDeactivation() throws Exception {
        var admin = account("admin", "ADMIN");
        String token = bearer(admin);
        mvc.perform(put("/api/admin/accounts/{id}/status?status=0", admin.getId()).header("Authorization", token))
                .andExpect(status().isBadRequest());
        mvc.perform(delete("/api/admin/accounts/{id}", admin.getId()).header("Authorization", token))
                .andExpect(status().isBadRequest());
        assertThat(accounts.findById(admin.getId()).orElseThrow().isActive()).isTrue();
        var second = account("second", "ADMIN");
        mvc.perform(put("/api/admin/accounts/{id}/status?status=0", admin.getId()).header("Authorization", token))
                .andExpect(status().isOk());
        mvc.perform(get("/api/admin/stats?year=2026").header("Authorization", token)).andExpect(status().isUnauthorized());
        mvc.perform(put("/api/admin/accounts/{id}/status?status=0", second.getId()).header("Authorization", bearer(second)))
                .andExpect(status().isBadRequest());
    }

    @Test void inactiveAdminDoesNotCountAsRecoveryPrincipal() throws Exception {
        var admin = account("admin", "ADMIN");
        var inactive = account("inactive", "ADMIN");
        inactive.deactivate(); accounts.saveAndFlush(inactive);
        mvc.perform(put("/api/admin/accounts/{id}/status?status=0", admin.getId()).header("Authorization", bearer(admin)))
                .andExpect(status().isBadRequest());
    }

    @Test void caseInsensitiveLegacyAdminStillGetsCanonicalAuthority() {
        var admin = account("legacy-admin", "admin");
        assertThat(authorization.findAuthorities(admin.getId())).contains("ROLE_ADMIN", "DASHBOARD_READ");
    }
}
