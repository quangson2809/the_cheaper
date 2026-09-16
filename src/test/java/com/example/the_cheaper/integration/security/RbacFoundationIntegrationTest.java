package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.config.OrderPermissionSeeder;
import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.AccountRoleEntity;
import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.entity.RolePermissionEntity;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.AccountRoleRepository;
import com.example.the_cheaper.repository.PermissionRepository;
import com.example.the_cheaper.repository.RolePermissionRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.service.authorization.AuthorizationQueryService;
import com.example.the_cheaper.testconfig.MySqlIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RbacFoundationIntegrationTest extends MySqlIntegrationTest {

    private static final List<String> TARGET_ORDER_PERMISSIONS = List.of(
            "USER_ORDER_CREATE",
            "USER_ORDER_READ",
            "USER_ORDER_CANCEL",
            "ORDER_READ",
            "ORDER_CONFIRM",
            "ORDER_CANCEL",
            "ORDER_DELIVERY_UPDATE",
            "ORDER_PAYMENT_COLLECT"
    );

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountRoleRepository accountRoleRepository;
    @Autowired
    private AuthorizationQueryService authorizationQueryService;
    @Autowired
    private OrderPermissionSeeder orderPermissionSeeder;

    @Test
    @Transactional
    void targetOrderPermissions_AreSeeded() {
        for (String code : TARGET_ORDER_PERMISSIONS) {
            assertThat(permissionRepository.findByCode(code))
                    .as("permission %s must exist", code)
                    .isPresent();
        }
    }

    @Test
    @Transactional
    void adminRole_ReceivesAllDefinedAuthoritiesWithoutRolePermissionRows() {
        RoleEntity adminRole = roleRepository.save(RoleEntity.builder()
                .name(Shared.ADMIN_ROLE)
                .description("Phase 3 admin")
                .build());
        AccountEntity admin = accountRepository.save(AccountEntity.builder()
                .name("Phase 3 Admin")
                .email("phase3-admin@example.com")
                .passwordHash("hash")
                .status(1)
                .build());
        accountRoleRepository.save(AccountRoleEntity.builder()
                .account(admin)
                .role(adminRole)
                .build());

        assertThat(rolePermissionRepository.findAllByRoleId(adminRole.getId())).isEmpty();

        Set<String> authorities = authorizationQueryService.findAuthorities(admin.getId());

        assertThat(authorities).contains("ROLE_ADMIN");
        assertThat(authorities).containsAll(TARGET_ORDER_PERMISSIONS);
    }

    @Test
    @Transactional
    void revokedStaffPermission_IsNotRestoredWhenPermissionSeederRunsAgain() {
        PermissionEntity orderConfirm = permissionRepository.findByCode("ORDER_CONFIRM")
                .orElseThrow();
        RoleEntity staffRole = roleRepository.save(RoleEntity.builder()
                .name("PHASE3_STAFF")
                .description("Phase 3 staff")
                .build());
        AccountEntity staff = accountRepository.save(AccountEntity.builder()
                .name("Phase 3 Staff")
                .email("phase3-staff@example.com")
                .passwordHash("hash")
                .status(1)
                .build());
        accountRoleRepository.save(AccountRoleEntity.builder()
                .account(staff)
                .role(staffRole)
                .build());
        rolePermissionRepository.save(RolePermissionEntity.builder()
                .role(staffRole)
                .permission(orderConfirm)
                .build());
        rolePermissionRepository.flush();

        assertThat(authorizationQueryService.findAuthorities(staff.getId()))
                .contains("ORDER_CONFIRM");

        rolePermissionRepository.deleteByRoleIdAndPermissionId(
                staffRole.getId(), orderConfirm.getId());
        rolePermissionRepository.flush();

        assertThat(authorizationQueryService.findAuthorities(staff.getId()))
                .doesNotContain("ORDER_CONFIRM");

        orderPermissionSeeder.run();
        rolePermissionRepository.flush();

        assertThat(rolePermissionRepository.existsByRoleIdAndPermissionId(
                staffRole.getId(), orderConfirm.getId())).isFalse();
        assertThat(authorizationQueryService.findAuthorities(staff.getId()))
                .doesNotContain("ORDER_CONFIRM");
    }
}
