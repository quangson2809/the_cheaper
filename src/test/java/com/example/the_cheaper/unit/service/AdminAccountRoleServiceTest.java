package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.dto.request.admin.AssignAccountRoleRequest;
import com.example.the_cheaper.dto.response.admin.AdminAccountRoleResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.mapper.admin.AdminAccountMapper;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.service.admin.AdminProtectedAccess;
import com.example.the_cheaper.service.admin.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAccountRoleServiceTest {

    @Mock
    private AdminProtectedAccess adminProtectedAccess;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AdminAccountMapper adminAccountMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserService service;

    @Test
    void getAccountRole_ShouldReturnCurrentRole() {
        AccountEntity admin = new AccountEntity();
        RoleEntity role = RoleEntity.builder()
                .id(2L)
                .name("PRODUCT_MANAGER")
                .description("Manage products")
                .build();
        AccountEntity account = AccountEntity.builder()
                .id(10L)
                .role(role)
                .build();

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        AdminAccountRoleResponse result = service.getAccountRole(admin, 10L);

        assertThat(result.getAccountId()).isEqualTo(10L);
        assertThat(result.getRoleId()).isEqualTo(2L);
        assertThat(result.getRoleName()).isEqualTo("PRODUCT_MANAGER");
        verify(adminProtectedAccess).adminAccess(admin);
    }

    @Test
    void assignAccountRole_ShouldAssignRole() {
        AccountEntity admin = new AccountEntity();
        AccountEntity account = AccountEntity.builder().id(10L).build();
        RoleEntity role = RoleEntity.builder()
                .id(2L)
                .name("PRODUCT_MANAGER")
                .description("Manage products")
                .build();

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        when(accountRepository.save(any(AccountEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminAccountRoleResponse result = service.assignAccountRole(
                admin,
                10L,
                new AssignAccountRoleRequest(2L)
        );

        assertThat(account.getRole()).isSameAs(role);
        assertThat(result.getRoleId()).isEqualTo(2L);
        assertThat(result.getRoleName()).isEqualTo("PRODUCT_MANAGER");
        verify(accountRepository).save(account);
    }

    @Test
    void assignAccountRole_ShouldBeIdempotentForSameRole() {
        AccountEntity admin = new AccountEntity();
        RoleEntity role = RoleEntity.builder().id(2L).name("PRODUCT_MANAGER").build();
        AccountEntity account = AccountEntity.builder().id(10L).role(role).build();

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));

        service.assignAccountRole(admin, 10L, new AssignAccountRoleRequest(2L));

        verify(accountRepository, never()).save(any(AccountEntity.class));
    }
}
