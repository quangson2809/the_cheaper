package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.UpdateRolePermissionsRequest;
import com.example.the_cheaper.dto.response.admin.AdminRolePermissionResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.entity.RolePermissionEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.repository.PermissionRepository;
import com.example.the_cheaper.repository.RolePermissionRepository;
import com.example.the_cheaper.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            throw new InvalidInputException("Permission ids không được trùng nhau");
        }

        List<PermissionEntity> permissions = loadPermissions(uniquePermissionIds);

        List<RolePermissionEntity> currentAssignments =
                rolePermissionRepository.findAllByRoleId(roleId);
        rolePermissionRepository.deleteAll(currentAssignments);

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

    @Transactional
    public AdminRolePermissionResponse grantPermission(
            Long roleId,
            Long permissionId,
            AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        RoleEntity role = getRole(roleId);
        PermissionEntity permission = getPermission(permissionId);

        if (rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            throw new ResourceAlreadyExistsException(
                    "Permission '" + permission.getCode() + "' đã được cấp cho role '" + role.getName() + "'");
        }

        rolePermissionRepository.save(RolePermissionEntity.builder()
                .role(role)
                .permission(permission)
                .build());

        return toResponse(permission);
    }

    @Transactional
    public void revokePermission(
            Long roleId,
            Long permissionId,
            AccountEntity currentUser) {
        adminProtectedAccess.adminAccess(currentUser);
        getRole(roleId);
        getPermission(permissionId);

        RolePermissionEntity assignment = rolePermissionRepository
                .findByRoleIdAndPermissionId(roleId, permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Permission chưa được cấp cho role"));

        rolePermissionRepository.delete(assignment);
    }

    private List<PermissionEntity> loadPermissions(Set<Long> permissionIds) {
        if (permissionIds.isEmpty()) {
            return List.of();
        }

        List<PermissionEntity> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            Set<Long> foundIds = permissions.stream()
                    .map(PermissionEntity::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingIds = new HashSet<>(permissionIds);
            missingIds.removeAll(foundIds);
            throw new ResourceNotFoundException(
                    "Không tìm thấy permission với id: " + missingIds);
        }
        return permissions;
    }

    private RoleEntity getRole(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy role với id: " + roleId));
    }

    private PermissionEntity getPermission(Long permissionId) {
        return permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy permission với id: " + permissionId));
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
