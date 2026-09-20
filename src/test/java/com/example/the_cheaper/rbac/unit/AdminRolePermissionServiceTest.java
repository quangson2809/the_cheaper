package com.example.the_cheaper.rbac.unit;

import com.example.the_cheaper.rbac.dto.request.admin.UpdateRolePermissionsRequest;
import com.example.the_cheaper.rbac.dto.response.admin.AdminRolePermissionResponse;
import com.example.the_cheaper.account.entity.AccountEntity;
import com.example.the_cheaper.rbac.entity.PermissionEntity;
import com.example.the_cheaper.rbac.entity.RoleEntity;
import com.example.the_cheaper.rbac.entity.RolePermissionEntity;
import com.example.the_cheaper.common.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.common.exception.ResourceNotFoundException;
import com.example.the_cheaper.rbac.repository.PermissionRepository;
import com.example.the_cheaper.rbac.repository.RolePermissionRepository;
import com.example.the_cheaper.rbac.repository.RoleRepository;
import com.example.the_cheaper.rbac.service.AdminRolePermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminRolePermissionServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private AdminRolePermissionService service;

    @Test
    void getPermissions_ShouldReturnPermissionsOfRole() {
        AccountEntity admin = new AccountEntity();
        RoleEntity role = RoleEntity.builder().id(1L).name("ADMIN").build();
        PermissionEntity permission = PermissionEntity.builder()
                .id(10L).name("Xem sản phẩm").code("PRODUCT_READ")
                .description("Read products").build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(rolePermissionRepository.findAllByRoleId(1L)).thenReturn(List.of(
                RolePermissionEntity.builder().id(100L).role(role).permission(permission).build()
        ));

        List<AdminRolePermissionResponse> result = service.getPermissions(1L, admin);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("PRODUCT_READ");
    }

    @Test
    void replacePermissions_ShouldReplaceExistingAssignmentsAtomically() {
        AccountEntity admin = new AccountEntity();
        RoleEntity role = RoleEntity.builder().id(1L).name("ADMIN").build();
        PermissionEntity permission1 = PermissionEntity.builder().id(10L).name("Read").code("PRODUCT_READ").build();
        PermissionEntity permission2 = PermissionEntity.builder().id(20L).name("Create").code("PRODUCT_CREATE").build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findAllById(anySet())).thenReturn(List.of(permission1, permission2));
        when(rolePermissionRepository.findAllByRoleId(1L)).thenReturn(List.of());
        when(rolePermissionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<AdminRolePermissionResponse> result = service.replacePermissions(
                1L, new UpdateRolePermissionsRequest(List.of(10L, 20L)), admin);

        assertThat(result).extracting(AdminRolePermissionResponse::getCode)
                .containsExactlyInAnyOrder("PRODUCT_READ", "PRODUCT_CREATE");
        verify(rolePermissionRepository).deleteAll(anyList());
        verify(rolePermissionRepository).saveAll(anyList());
    }

    @Test
    void replacePermissions_ShouldRejectDuplicatePermissionIds() {
        AccountEntity admin = new AccountEntity();
        RoleEntity role = RoleEntity.builder().id(1L).name("ADMIN").build();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        assertThatThrownBy(() -> service.replacePermissions(
                1L, new UpdateRolePermissionsRequest(List.of(10L, 10L)), admin))
                .isInstanceOf(com.example.the_cheaper.common.exception.InvalidInputException.class);

        verifyNoInteractions(permissionRepository, rolePermissionRepository);
    }

    @Test
    void grantPermission_ShouldCreateAssignment() {
        AccountEntity admin = new AccountEntity();
        RoleEntity role = RoleEntity.builder().id(1L).name("ADMIN").build();
        PermissionEntity permission = PermissionEntity.builder().id(10L).name("Read").code("PRODUCT_READ").build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(10L)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.existsByRoleIdAndPermissionId(1L, 10L)).thenReturn(false);
        when(rolePermissionRepository.save(any(RolePermissionEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdminRolePermissionResponse result = service.grantPermission(1L, 10L, admin);

        assertThat(result.getCode()).isEqualTo("PRODUCT_READ");
        verify(rolePermissionRepository).save(any(RolePermissionEntity.class));
    }

    @Test
    void grantPermission_ShouldRejectExistingAssignment() {
        AccountEntity admin = new AccountEntity();
        RoleEntity role = RoleEntity.builder().id(1L).name("ADMIN").build();
        PermissionEntity permission = PermissionEntity.builder().id(10L).name("Read").code("PRODUCT_READ").build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(10L)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.existsByRoleIdAndPermissionId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.grantPermission(1L, 10L, admin))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(rolePermissionRepository, never()).save(any());
    }

    @Test
    void revokePermission_ShouldDeleteExistingAssignment() {
        AccountEntity admin = new AccountEntity();
        RoleEntity role = RoleEntity.builder().id(1L).name("ADMIN").build();
        PermissionEntity permission = PermissionEntity.builder().id(10L).name("Read").code("PRODUCT_READ").build();
        RolePermissionEntity assignment = RolePermissionEntity.builder()
                .id(100L).role(role).permission(permission).build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(10L)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.findByRoleIdAndPermissionId(1L, 10L))
                .thenReturn(Optional.of(assignment));

        service.revokePermission(1L, 10L, admin);

        verify(rolePermissionRepository).delete(assignment);
    }

    @Test
    void revokePermission_ShouldRejectWhenAssignmentDoesNotExist() {
        AccountEntity admin = new AccountEntity();
        RoleEntity role = RoleEntity.builder().id(1L).name("ADMIN").build();
        PermissionEntity permission = PermissionEntity.builder().id(10L).name("Read").code("PRODUCT_READ").build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(10L)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.findByRoleIdAndPermissionId(1L, 10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.revokePermission(1L, 10L, admin))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(rolePermissionRepository, never()).delete(any());
    }
}
