package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.UpdateRolePermissionsRequest;
import com.example.the_cheaper.dto.response.admin.AdminRolePermissionResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.entity.RolePermissionEntity;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.repository.PermissionRepository;
import com.example.the_cheaper.repository.RolePermissionRepository;
import com.example.the_cheaper.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AdminProtectedAccess adminProtectedAccess;

    @Transactional(readOnly = true)
    public List<AdminRolePermissionResponse> getPermissions(
            Long roleId,
            AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        getRole(roleId);

        return rolePermissionRepository.findAllByRoleId(roleId).stream()
                .map(RolePermissionEntity::getPermission)
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<AdminRolePermissionResponse> replacePermissions(
            Long roleId,
            UpdateRolePermissionsRequest request,
            AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        RoleEntity role = getRole(roleId);

        List<Long> permissionIds = request.getPermissionIds() == null
                ? List.of()
                : request.getPermissionIds();

        Set<Long> uniquePermissionIds = new HashSet<>(permissionIds);
        if (uniquePermissionIds.size() != permissionIds.size()) {
            throw new IllegalArgumentException("Permission ids không được trùng nhau");
        }

        List<PermissionEntity> permissions = uniquePermissionIds.isEmpty()
                ? List.of()
                : permissionRepository.findAllById(uniquePermissionIds);

        if (permissions.size() != uniquePermissionIds.size()) {
            Set<Long> foundIds = permissions.stream()
                    .map(PermissionEntity::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingIds = new HashSet<>(uniquePermissionIds);
            missingIds.removeAll(foundIds);
            throw new ResourceNotFoundException(
                    "Không tìm thấy permission với id: " + missingIds);
        }

        rolePermissionRepository.deleteAll(rolePermissionRepository.findAllByRoleId(roleId));

        List<RolePermissionEntity> assignments = permissions.stream()
                .map(permission -> RolePermissionEntity.builder()
                        .role(role)
                        .permission(permission)
                        .build())
                .toList();

        rolePermissionRepository.saveAll(assignments);

        return permissions.stream()
                .map(this::toResponse)
                .toList();
    }

    private RoleEntity getRole(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy role với id: " + roleId));
    }

    private AdminRolePermissionResponse toResponse(PermissionEntity permission) {
        return AdminRolePermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .code(permission.getCode())
                .description(permission.getDescription())
                .build();
    }
}
