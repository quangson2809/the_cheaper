package com.example.the_cheaper.config;

import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.AccountRoleEntity;
import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.entity.RolePermissionEntity;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.AccountRoleRepository;
import com.example.the_cheaper.repository.PermissionRepository;
import com.example.the_cheaper.repository.RolePermissionRepository;
import com.example.the_cheaper.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
@Order(2)
public class RbacDataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        RoleEntity userRole = findOrCreateRole(Shared.USER_ROLE, "Khách hàng sử dụng các chức năng phía client");
        RoleEntity adminRole = findOrCreateRole(Shared.ADMIN_ROLE, "Quản trị viên hệ thống");

        Map<String, PermissionEntity> permissions = seedPermissions();
        seedRolePermissions(adminRole, permissions.values().stream().toList());
        seedAccountRoles(userRole, adminRole);

        log.info("RBAC seed completed: {} permissions, ADMIN permissions assigned, account roles synchronized",
                permissions.size());
    }

    private RoleEntity findOrCreateRole(String name, String description) {
        RoleEntity role = roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder()
                        .name(name)
                        .description(description)
                        .build()));

        if (role.getDescription() == null || role.getDescription().isBlank()) {
            role.setDescription(description);
            role = roleRepository.save(role);
        }

        return role;
    }

    private Map<String, PermissionEntity> seedPermissions() {
        List<PermissionDefinition> definitions = List.of(
                new PermissionDefinition("ACCOUNT_READ", "Xem tài khoản", "Xem danh sách và thông tin tài khoản"),
                new PermissionDefinition("ACCOUNT_CREATE", "Tạo tài khoản", "Tạo tài khoản mới"),
                new PermissionDefinition("ACCOUNT_UPDATE", "Cập nhật tài khoản", "Cập nhật thông tin tài khoản"),
                new PermissionDefinition("ACCOUNT_DELETE", "Xóa tài khoản", "Xóa tài khoản đã ngừng hoạt động"),
                new PermissionDefinition("ACCOUNT_ASSIGN_ROLE", "Gán role cho tài khoản", "Gán hoặc thay đổi role của tài khoản"),
                new PermissionDefinition("ROLE_READ", "Xem role", "Xem danh sách và thông tin role"),
                new PermissionDefinition("ROLE_CREATE", "Tạo role", "Tạo role mới"),
                new PermissionDefinition("ROLE_UPDATE", "Cập nhật role", "Cập nhật thông tin role"),
                new PermissionDefinition("ROLE_DELETE", "Xóa role", "Xóa role"),
                new PermissionDefinition("ROLE_ASSIGN_PERMISSION", "Gán permission cho role", "Gán hoặc thu hồi permission của role"),
                new PermissionDefinition("PERMISSION_READ", "Xem permission", "Xem danh sách và thông tin permission"),
                new PermissionDefinition("PERMISSION_CREATE", "Tạo permission", "Tạo permission mới"),
                new PermissionDefinition("PERMISSION_UPDATE", "Cập nhật permission", "Cập nhật thông tin permission"),
                new PermissionDefinition("PERMISSION_DELETE", "Xóa permission", "Xóa permission")
        );

        Map<String, PermissionEntity> result = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(PermissionEntity::getCode, Function.identity()));

        for (PermissionDefinition definition : definitions) {
            result.computeIfAbsent(definition.code(), code -> permissionRepository.save(
                    PermissionEntity.builder()
                            .name(definition.name())
                            .code(code)
                            .description(definition.description())
                            .build()));
        }

        return result;
    }

    private void seedRolePermissions(RoleEntity role, List<PermissionEntity> permissions) {
        for (PermissionEntity permission : permissions) {
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(role.getId(), permission.getId())) {
                rolePermissionRepository.save(RolePermissionEntity.builder()
                        .role(role)
                        .permission(permission)
                        .build());
            }
        }
    }

    private void seedAccountRoles(RoleEntity userRole, RoleEntity adminRole) {
        accountRepository.findByEmail("admin@gmail.com")
                .ifPresent(account -> ensureAccountRole(account, adminRole));

        accountRepository.findAll().stream()
                .filter(account -> !"admin@gmail.com".equalsIgnoreCase(account.getEmail()))
                .forEach(account -> ensureAccountRole(account, userRole));
    }

    private void ensureAccountRole(AccountEntity account, RoleEntity role) {
        if (!accountRoleRepository.existsByAccountIdAndRoleId(account.getId(), role.getId())) {
            accountRoleRepository.save(AccountRoleEntity.builder()
                    .account(account)
                    .role(role)
                    .build());
        }
    }

    private record PermissionDefinition(String code, String name, String description) {
    }
}
