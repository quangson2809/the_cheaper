package com.example.the_cheaper.config;

import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.entity.RolePermissionEntity;
import com.example.the_cheaper.repository.PermissionRepository;
import com.example.the_cheaper.repository.RolePermissionRepository;
import com.example.the_cheaper.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.annotation.Order;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(100)
public class OrderPermissionSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        PermissionCatalog.DEFINITIONS.forEach(definition -> findOrCreatePermission(
                definition.code(), definition.name(), definition.description()));
    }

    /**
     * USER_ORDER_* là quyền nền tảng của role USER. Chạy sau toàn bộ CommandLineRunner
     * để hoạt động cả trên database mới, nơi DataSeeder có thể tạo role USER muộn hơn.
     * Các quyền nhân viên ORDER_* tuyệt đối không được tự động gán ở đây.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void ensureBaselineUserPermissions() {
        roleRepository.findByName(Shared.USER_ROLE).ifPresent(userRole -> {
            ensureRolePermission(userRole, requirePermission("USER_ORDER_READ"));
            ensureRolePermission(userRole, requirePermission("USER_ORDER_CREATE"));
            ensureRolePermission(userRole, requirePermission("USER_ORDER_CANCEL"));
        });
    }

    private PermissionEntity findOrCreatePermission(String code, String name, String description) {
        PermissionEntity permission = permissionRepository.findByCode(code)
                .orElseGet(() -> permissionRepository.save(PermissionEntity.builder()
                        .name(name)
                        .code(code)
                        .description(description)
                        .build()));

        boolean changed = false;
        if (permission.getName() == null || permission.getName().isBlank()) {
            permission.setName(name);
            changed = true;
        }
        if (permission.getDescription() == null || permission.getDescription().isBlank()) {
            permission.setDescription(description);
            changed = true;
        }
        return changed ? permissionRepository.save(permission) : permission;
    }

    private PermissionEntity requirePermission(String code) {
        return permissionRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException(
                        "Permission nền tảng chưa được seed: " + code));
    }

    private void ensureRolePermission(RoleEntity role, PermissionEntity permission) {
        if (!rolePermissionRepository.existsByRoleIdAndPermissionId(role.getId(), permission.getId())) {
            rolePermissionRepository.save(RolePermissionEntity.builder()
                    .role(role)
                    .permission(permission)
                    .build());
        }
    }
}
