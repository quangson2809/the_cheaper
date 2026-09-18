package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.security.CustomUserDetailsService;
import com.example.the_cheaper.service.admin.AdminDashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DashboardAuthorizationIntegrationTest extends RbacIntegrationSupport {
    @Autowired AdminDashboardService dashboard;
    @Autowired CustomUserDetailsService details;

    @ParameterizedTest
    @ValueSource(strings = {"stats?year=2026", "monthly-revenue?year=2026", "monthly-quantity?year=2026", "order-status"})
    void realJwtProtectsEveryDashboardRoute(String route) throws Exception {
        String url = "/api/admin/" + route;
        var user = account("user", "USER");
        var staff = account("staff", "DASHBOARD_STAFF");
        var admin = account("admin", "ADMIN");
        String token = bearer(staff);
        mvc.perform(get(url)).andExpect(status().isUnauthorized());
        mvc.perform(get(url).header("Authorization", bearer(user))).andExpect(status().isForbidden());
        mvc.perform(get(url).header("Authorization", token)).andExpect(status().isForbidden());
        grant(staff.getAccountRoles().get(0).getRole(), "DASHBOARD_READ");
        mvc.perform(get(url).header("Authorization", token)).andExpect(status().isOk());
        assertThat(grants.findAllByRoleId(admin.getAccountRoles().get(0).getRole().getId())).isEmpty();
        mvc.perform(get(url).header("Authorization", bearer(admin))).andExpect(status().isOk());
        grants.deleteByRoleIdAndPermissionId(staff.getAccountRoles().get(0).getRole().getId(),
                permissions.findByCode("DASHBOARD_READ").orElseThrow().getId());
        grants.flush();
        mvc.perform(get(url).header("Authorization", token)).andExpect(status().isForbidden());
        String adminToken = bearer(admin);
        admin.deactivate(); accounts.saveAndFlush(admin);
        mvc.perform(get(url).header("Authorization", adminToken)).andExpect(status().isUnauthorized());
    }

    @Test void directServiceCallCannotUseAnAdminArgumentToBypassAuthentication() {
        var staff = account("staff", "DASHBOARD_STAFF");
        var admin = account("admin", "ADMIN");
        var principal = details.loadUserByUsername(staff.getEmail());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        try {
            assertThatThrownBy(() -> dashboard.getDashboardStats(admin, 2026)).isInstanceOf(AccessDeniedException.class);
            assertThatThrownBy(() -> dashboard.getMonthlyRevenue(2026, admin)).isInstanceOf(AccessDeniedException.class);
            assertThatThrownBy(() -> dashboard.getMonthlySoldQuantity(2026, admin)).isInstanceOf(AccessDeniedException.class);
            assertThatThrownBy(() -> dashboard.getOrderStatusRatios(admin)).isInstanceOf(AccessDeniedException.class);
        } finally { SecurityContextHolder.clearContext(); }
    }
}
