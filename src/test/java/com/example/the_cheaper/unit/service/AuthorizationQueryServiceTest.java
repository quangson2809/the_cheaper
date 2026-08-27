package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.repository.AccountRoleRepository;
import com.example.the_cheaper.service.authorization.AuthorizationQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationQueryServiceTest {

    @Mock
    private AccountRoleRepository accountRoleRepository;

    @InjectMocks
    private AuthorizationQueryService service;

    @Test
    void findAuthorities_ShouldMergeRolesAndPermissions() {
        when(accountRoleRepository.findRoleNamesByAccountId(1L))
                .thenReturn(List.of("ADMIN", "STAFF"));
        when(accountRoleRepository.findPermissionCodesByAccountId(1L))
                .thenReturn(List.of("PRODUCT_READ", "PRODUCT_DELETE"));

        Set<String> authorities = service.findAuthorities(1L);

        assertThat(authorities).containsExactlyInAnyOrder(
                "ROLE_ADMIN",
                "ROLE_STAFF",
                "PRODUCT_READ",
                "PRODUCT_DELETE");
    }
}
