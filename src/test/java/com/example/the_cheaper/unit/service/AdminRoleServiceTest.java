package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.dto.request.admin.AdminRoleCreateRequest;
import com.example.the_cheaper.dto.request.admin.AdminRoleUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminRoleResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminRoleMapper;
import com.example.the_cheaper.repository.RolePermissionRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.service.admin.AdminRoleService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminRoleServiceTest {

//    @Mock
//    private RoleRepository roleRepository;
//
//    @Mock
//    private RolePermissionRepository rolePermissionRepository;
//
//    @Mock
//    private AdminRoleMapper roleMapper;
//
//    @Mock
//    private AdminProtectedAccess adminProtectedAccess;
//
//    @InjectMocks
//    private AdminRoleService service;
//
//    @Test
//    void listRoles_ShouldReturnRoles() {
//        AccountEntity admin = new AccountEntity();
//        RoleEntity role = RoleEntity.builder().id(1L).name("ADMIN").build();
//        AdminRoleResponse response = AdminRoleResponse.builder().id(1L).name("ADMIN").build();
//
//        when(roleRepository.findAll()).thenReturn(List.of(role));
//        when(roleMapper.toResponse(role)).thenReturn(response);
//
//        List<AdminRoleResponse> result = service.listRoles(admin);
//
//        assertThat(result).containsExactly(response);
//        verify(adminProtectedAccess).adminAccess(admin);
//    }
//
//    @Test
//    void getRole_ShouldThrowWhenNotFound() {
//        AccountEntity admin = new AccountEntity();
//        when(roleRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> service.getRole(99L, admin))
//                .isInstanceOf(ResourceNotFoundException.class);
//    }
//
//    @Test
//    void createRole_ShouldCreateRole() {
//        AccountEntity admin = new AccountEntity();
//        AdminRoleCreateRequest request = AdminRoleCreateRequest.builder()
//                .name("PRODUCT_MANAGER").description("Manage products").build();
//        RoleEntity entity = RoleEntity.builder().name("PRODUCT_MANAGER").build();
//        AdminRoleResponse response = AdminRoleResponse.builder().id(2L).name("PRODUCT_MANAGER").build();
//
//        when(roleRepository.existsByName("PRODUCT_MANAGER")).thenReturn(false);
//        when(roleMapper.toEntity(request)).thenReturn(entity);
//        when(roleRepository.save(entity)).thenReturn(entity);
//        when(roleMapper.toResponse(entity)).thenReturn(response);
//
//        assertThat(service.createRole(request, admin)).isEqualTo(response);
//        verify(roleRepository).save(entity);
//    }
//
//    @Test
//    void createRole_ShouldRejectDuplicateName() {
//        AccountEntity admin = new AccountEntity();
//        AdminRoleCreateRequest request = AdminRoleCreateRequest.builder().name("ADMIN").build();
//        when(roleRepository.existsByName("ADMIN")).thenReturn(true);
//
//        assertThatThrownBy(() -> service.createRole(request, admin))
//                .isInstanceOf(ResourceAlreadyExistsException.class);
//
//        verify(roleRepository, never()).save(any());
//    }
//
//    @Test
//    void updateRole_ShouldRejectDuplicateNameOwnedByAnotherRole() {
//        AccountEntity admin = new AccountEntity();
//        RoleEntity current = RoleEntity.builder().id(2L).name("PRODUCT_MANAGER").build();
//        RoleEntity other = RoleEntity.builder().id(1L).name("ADMIN").build();
//        AdminRoleUpdateRequest request = AdminRoleUpdateRequest.builder().name("ADMIN").build();
//
//        when(roleRepository.findById(2L)).thenReturn(Optional.of(current));
//        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(other));
//
//        assertThatThrownBy(() -> service.updateRole(2L, request, admin))
//                .isInstanceOf(ResourceAlreadyExistsException.class);
//
//        verify(roleMapper, never()).updateEntityFromRequest(any(), any());
//    }
//
//    @Test
//    void deleteRole_ShouldRejectWhenAssignedToAccount() {
//        AccountEntity admin = new AccountEntity();
//        when(roleRepository.existsById(2L)).thenReturn(true);
//        when(roleRepository.existsAssignedToAccount(2L)).thenReturn(true);
//
//        assertThatThrownBy(() -> service.deleteRole(2L, admin))
//                .isInstanceOf(ResourceAlreadyExistsException.class);
//
//        verify(roleRepository, never()).deleteById(2L);
//    }
//
//    @Test
//    void deleteRole_ShouldRejectWhenRoleHasPermissions() {
//        AccountEntity admin = new AccountEntity();
//        when(roleRepository.existsById(2L)).thenReturn(true);
//        when(roleRepository.existsAssignedToAccount(2L)).thenReturn(false);
//        when(rolePermissionRepository.existsByRoleId(2L)).thenReturn(true);
//
//        assertThatThrownBy(() -> service.deleteRole(2L, admin))
//                .isInstanceOf(ResourceAlreadyExistsException.class);
//
//        verify(roleRepository, never()).deleteById(2L);
//    }
//
//    @Test
//    void deleteRole_ShouldDeleteUnusedRole() {
//        AccountEntity admin = new AccountEntity();
//        when(roleRepository.existsById(2L)).thenReturn(true);
//        when(roleRepository.existsAssignedToAccount(2L)).thenReturn(false);
//        when(rolePermissionRepository.existsByRoleId(2L)).thenReturn(false);
//
//        service.deleteRole(2L, admin);
//
//        verify(roleRepository).deleteById(2L);
//    }
}
