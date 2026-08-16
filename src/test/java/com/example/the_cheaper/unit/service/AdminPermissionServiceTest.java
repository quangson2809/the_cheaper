package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.dto.request.admin.AdminPermissionCreateRequest;
import com.example.the_cheaper.dto.request.admin.AdminPermissionUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminPermissionResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.mapper.admin.AdminPermissionMapper;
import com.example.the_cheaper.repository.PermissionRepository;
import com.example.the_cheaper.service.admin.AdminPermissionService;
import com.example.the_cheaper.service.admin.AdminProtectedAccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminPermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private AdminPermissionMapper permissionMapper;

    @Mock
    private AdminProtectedAccess adminProtectedAccess;

    @InjectMocks
    private AdminPermissionService adminPermissionService;

    @Test
    @DisplayName("createPermission - should create permission when name and code are unique")
    void createPermission_ShouldCreateWhenUnique() {
        AccountEntity admin = new AccountEntity();
        AdminPermissionCreateRequest request = AdminPermissionCreateRequest.builder()
                .name("Xem sản phẩm")
                .code("PRODUCT_READ")
                .description("Cho phép xem sản phẩm")
                .build();
        PermissionEntity entity = PermissionEntity.builder()
                .id(1L)
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .build();
        AdminPermissionResponse response = AdminPermissionResponse.builder()
                .id(1L)
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .build();

        when(permissionRepository.existsByName(request.getName())).thenReturn(false);
        when(permissionRepository.existsByCode(request.getCode())).thenReturn(false);
        when(permissionMapper.toEntity(request)).thenReturn(entity);
        when(permissionRepository.save(entity)).thenReturn(entity);
        when(permissionMapper.toResponse(entity)).thenReturn(response);

        AdminPermissionResponse result =
                adminPermissionService.createPermission(request, admin);

        assertThat(result).isEqualTo(response);
        verify(adminProtectedAccess).adminAccess(admin);
        verify(permissionRepository).save(entity);
    }

    @Test
    @DisplayName("createPermission - should reject duplicate name")
    void createPermission_ShouldRejectDuplicateName() {
        AccountEntity admin = new AccountEntity();
        AdminPermissionCreateRequest request = AdminPermissionCreateRequest.builder()
                .name("Xem sản phẩm")
                .code("PRODUCT_READ")
                .build();

        when(permissionRepository.existsByName(request.getName())).thenReturn(true);

        assertThatThrownBy(() -> adminPermissionService.createPermission(request, admin))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Xem sản phẩm");

        verify(permissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("getPermission - should return permission when found")
    void getPermission_ShouldReturnWhenFound() {
        AccountEntity admin = new AccountEntity();
        PermissionEntity entity = PermissionEntity.builder().id(1L).build();
        AdminPermissionResponse response = AdminPermissionResponse.builder().id(1L).build();

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(permissionMapper.toResponse(entity)).thenReturn(response);

        AdminPermissionResponse result = adminPermissionService.getPermission(1L, admin);

        assertThat(result).isEqualTo(response);
        verify(adminProtectedAccess).adminAccess(admin);
    }

    @Test
    @DisplayName("getPermission - should throw when permission does not exist")
    void getPermission_ShouldThrowWhenNotFound() {
        AccountEntity admin = new AccountEntity();
        when(permissionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminPermissionService.getPermission(99L, admin))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updatePermission - should reject duplicate code")
    void updatePermission_ShouldRejectDuplicateCode() {
        AccountEntity admin = new AccountEntity();
        PermissionEntity entity = PermissionEntity.builder()
                .id(1L)
                .name("Xem sản phẩm")
                .code("PRODUCT_READ")
                .build();
        AdminPermissionUpdateRequest request = AdminPermissionUpdateRequest.builder()
                .name("Xem sản phẩm mới")
                .code("PRODUCT_WRITE")
                .build();

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(permissionRepository.existsByNameAndIdNot(request.getName(), 1L)).thenReturn(false);
        when(permissionRepository.existsByCodeAndIdNot(request.getCode(), 1L)).thenReturn(true);

        assertThatThrownBy(() -> adminPermissionService.updatePermission(1L, request, admin))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("PRODUCT_WRITE");

        verify(permissionMapper, never()).updateEntityFromRequest(any(), any());
    }

    @Test
    @DisplayName("deletePermission - should delete existing permission")
    void deletePermission_ShouldDeleteExisting() {
        AccountEntity admin = new AccountEntity();
        when(permissionRepository.existsById(1L)).thenReturn(true);

        adminPermissionService.deletePermission(1L, admin);

        verify(adminProtectedAccess).adminAccess(admin);
        verify(permissionRepository).deleteById(1L);
    }
}
