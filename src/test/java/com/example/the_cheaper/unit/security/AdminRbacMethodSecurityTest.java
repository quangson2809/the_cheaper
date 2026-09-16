package com.example.the_cheaper.unit.security;

import com.example.the_cheaper.controller.admin.AdminPermissionController;
import com.example.the_cheaper.controller.admin.AdminRolePermissionController;
import com.example.the_cheaper.dto.request.admin.AdminPermissionCreateRequest;
import com.example.the_cheaper.dto.request.admin.AdminPermissionUpdateRequest;
import com.example.the_cheaper.dto.request.admin.UpdateRolePermissionsRequest;
import com.example.the_cheaper.service.admin.AdminPermissionService;
import com.example.the_cheaper.service.admin.AdminRolePermissionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/** Replaces assertions against the removed AdminProtectedAccess with real Spring proxies. */
@SpringJUnitConfig(AdminRbacMethodSecurityTest.Config.class)
class AdminRbacMethodSecurityTest {
    @Configuration(proxyBeanMethods = false)
    @EnableMethodSecurity
    @Import({AdminPermissionController.class, AdminRolePermissionController.class})
    static class Config {
        @Bean AdminPermissionService permissionService() { return mock(AdminPermissionService.class); }
        @Bean AdminRolePermissionService rolePermissionService() { return mock(AdminRolePermissionService.class); }
    }

    @Autowired AdminPermissionController permissions;
    @Autowired AdminRolePermissionController rolePermissions;
    @Autowired AdminPermissionService permissionService;
    @Autowired AdminRolePermissionService rolePermissionService;

    @BeforeEach
    void resetInteractions() {
        clearInvocations(permissionService, rolePermissionService);
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @CsvSource({
            "list,PERMISSION_READ", "get,PERMISSION_READ", "create,PERMISSION_CREATE",
            "update,PERMISSION_UPDATE", "delete,PERMISSION_DELETE", "roleRead,ROLE_PERMISSION_READ",
            "roleReplace,ROLE_PERMISSION_UPDATE", "roleGrant,ROLE_PERMISSION_GRANT",
            "roleRevoke,ROLE_PERMISSION_REVOKE"
    })
    void permissionIsRequiredBeforeCallingService(String action, String permission) {
        authenticate("UNRELATED_PERMISSION");
        assertThrows(AccessDeniedException.class, () -> invoke(action));
        verifyNoInteractions(permissionService, rolePermissionService);

        authenticate(permission);
        assertDoesNotThrow(() -> invoke(action));
        switch (action) {
            case "list" -> verify(permissionService).listPermissions(0, 20, null);
            case "get" -> verify(permissionService).getPermission(1L, null);
            case "create" -> verify(permissionService).createPermission(any(), isNull());
            case "update" -> verify(permissionService).updatePermission(eq(1L), any(), isNull());
            case "delete" -> verify(permissionService).deletePermission(1L, null);
            case "roleRead" -> verify(rolePermissionService).getPermissions(1L, null);
            case "roleReplace" -> verify(rolePermissionService).replacePermissions(eq(1L), any(), isNull());
            case "roleGrant" -> verify(rolePermissionService).grantPermission(1L, 2L, null);
            case "roleRevoke" -> verify(rolePermissionService).revokePermission(1L, 2L, null);
            default -> throw new IllegalArgumentException(action);
        }
    }

    private void authenticate(String permission) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test-user", "unused",
                        AuthorityUtils.createAuthorityList(permission)));
    }

    private void invoke(String action) {
        switch (action) {
            case "list" -> permissions.listPermissions(0, 20, null);
            case "get" -> permissions.getPermission(1L, null);
            case "create" -> permissions.createPermission(AdminPermissionCreateRequest.builder().build(), null);
            case "update" -> permissions.updatePermission(1L, AdminPermissionUpdateRequest.builder().build(), null);
            case "delete" -> permissions.deletePermission(1L, null);
            case "roleRead" -> rolePermissions.getPermissions(1L, null);
            case "roleReplace" -> rolePermissions.replacePermissions(1L, new UpdateRolePermissionsRequest(List.of(2L)), null);
            case "roleGrant" -> rolePermissions.grantPermission(1L, 2L, null);
            case "roleRevoke" -> rolePermissions.revokePermission(1L, 2L, null);
            default -> throw new IllegalArgumentException(action);
        }
    }
}
