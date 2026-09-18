package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.config.PermissionCatalog;
import com.example.the_cheaper.dto.request.admin.AdminPermissionUpdateRequest;
import com.example.the_cheaper.dto.request.admin.AdminRoleUpdateRequest;
import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.mapper.admin.AdminPermissionMapper;
import com.example.the_cheaper.mapper.admin.AdminRoleMapper;
import com.example.the_cheaper.repository.*;
import com.example.the_cheaper.service.admin.AdminPermissionService;
import com.example.the_cheaper.service.admin.AdminRoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SystemCatalogInvariantTest {
    @ParameterizedTest @ValueSource(strings = {"ADMIN", "USER", "admin", "user"})
    void roleProtectionAppliesEvenWithoutAnyAssignments(String name) {
        var roles = mock(RoleRepository.class);
        var mapper = mock(AdminRoleMapper.class);
        var service = new AdminRoleService(roles, mock(AccountRoleRepository.class), mock(RolePermissionRepository.class), mapper);
        when(roles.findById(72L)).thenReturn(Optional.of(RoleEntity.builder().id(72L).name(name).build()));
        assertThatThrownBy(() -> service.updateRole(72L, AdminRoleUpdateRequest.builder().name("OTHER").build(), null))
                .isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> service.deleteRole(72L, null)).isInstanceOf(InvalidInputException.class);
        verifyNoInteractions(mapper);
        verify(roles, never()).deleteById(any());
    }

    @Test void everyCanonicalPermissionCodeIsStableAndCannotBeDeleted() {
        var permissions = mock(PermissionRepository.class);
        var mapper = mock(AdminPermissionMapper.class);
        var service = new AdminPermissionService(permissions, mock(RolePermissionRepository.class), mapper);
        for (String code : PermissionCatalog.CODES) {
            when(permissions.findById(72L)).thenReturn(Optional.of(PermissionEntity.builder().id(72L).code(code).build()));
            assertThatThrownBy(() -> service.updatePermission(72L, AdminPermissionUpdateRequest.builder().code("OTHER").build(), null))
                    .as(code).isInstanceOf(InvalidInputException.class);
            assertThatThrownBy(() -> service.deletePermission(72L, null)).as(code).isInstanceOf(InvalidInputException.class);
        }
        verifyNoInteractions(mapper);
        verify(permissions, never()).deleteById(any());
    }
}
