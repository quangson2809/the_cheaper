package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.repository.AccountRoleRepository;
import com.example.the_cheaper.repository.PermissionRepository;
import com.example.the_cheaper.service.authorization.AuthorizationQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationQueryServiceTest {

    @Mock
    private AccountRoleRepository accountRoleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private AuthorizationQueryService service;

    @Test
    void findAuthorities_ShouldMergeNonAdminRolesAndAssignedPermissions() {
        when(accountRoleRepository.findRoleNamesByAccountId(1L))
                .thenReturn(List.of("STAFF", "WAREHOUSE"));
        when(accountRoleRepository.findPermissionCodesByAccountId(1L))
                .thenReturn(List.of("ORDER_READ", "ORDER_CONFIRM"));

        Set<String> authorities = service.findAuthorities(1L);

        assertThat(authorities).containsExactlyInAnyOrder(
                "ROLE_STAFF",
                "ROLE_WAREHOUSE",
                "ORDER_READ",
                "ORDER_CONFIRM");
        verify(permissionRepository, never()).findAll();
    }

    @Test
    void findAuthorities_AdminReceivesEveryDefinedPermissionWithoutRoleAssignments() {
        when(accountRoleRepository.findRoleNamesByAccountId(2L))
                .thenReturn(List.of("ADMIN"));
        when(permissionRepository.findAll()).thenReturn(List.of(
                PermissionEntity.builder().code("ORDER_CONFIRM").name("Confirm").build(),
                PermissionEntity.builder().code("ORDER_PAYMENT_COLLECT").name("Collect").build()
        ));

        Set<String> authorities = service.findAuthorities(2L);

        assertThat(authorities).containsExactlyInAnyOrder(
                "ROLE_ADMIN",
                "ORDER_CONFIRM",
                "ORDER_PAYMENT_COLLECT");
        verify(accountRoleRepository, never()).findPermissionCodesByAccountId(2L);
    }

    @Test
    void findAuthorities_ReflectsRevokedPermissionImmediately() {
        when(accountRoleRepository.findRoleNamesByAccountId(3L))
                .thenReturn(List.of("STAFF"));
        when(accountRoleRepository.findPermissionCodesByAccountId(3L))
                .thenReturn(List.of("ORDER_CANCEL"), List.of());

        assertThat(service.findAuthorities(3L)).contains("ORDER_CANCEL");
        assertThat(service.findAuthorities(3L)).doesNotContain("ORDER_CANCEL");
    }
}
