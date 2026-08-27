package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.dto.request.admin.AssignAccountRoleRequest;
import com.example.the_cheaper.dto.response.admin.AdminAccountRoleResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.AccountRoleEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.mapper.admin.AdminAccountMapper;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.AccountRoleRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.service.admin.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAccountRoleServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountRoleRepository accountRoleRepository;

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
        when(accountRoleRepository.findAllByAccountId(10L)).thenReturn(List.of(
                AccountRoleEntity.builder().account(account).role(role).build()));

        AdminAccountRoleResponse result = service.getAccountRole(10L);

        assertThat(result.getAccountId()).isEqualTo(10L);
        assertThat(result.getRoleId()).isEqualTo(2L);
        assertThat(result.getRoleName()).isEqualTo("PRODUCT_MANAGER");
    }

    @Test
    void assignAccountRole_ShouldReplaceCurrentRole() {
        AccountEntity account = AccountEntity.builder().id(10L).build();
        RoleEntity role = RoleEntity.builder()
                .id(2L)
                .name("PRODUCT_MANAGER")
                .description("Manage products")
                .build();

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));

        AdminAccountRoleResponse result = service.assignAccountRole(
                10L,
                new AssignAccountRoleRequest(2L));

        assertThat(account.getRole()).isSameAs(role);
        assertThat(result.getRoleId()).isEqualTo(2L);
        assertThat(result.getRoleName()).isEqualTo("PRODUCT_MANAGER");
        verify(accountRoleRepository).deleteAllByAccountId(10L);
        verify(accountRoleRepository).save(any(AccountRoleEntity.class));
        verify(accountRepository).save(account);
    }
}
