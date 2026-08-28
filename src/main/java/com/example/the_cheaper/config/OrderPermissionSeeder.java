package com.example.the_cheaper.config;

import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.entity.RolePermissionEntity;
import com.example.the_cheaper.repository.PermissionRepository;
import com.example.the_cheaper.repository.RolePermissionRepository;
import com.example.the_cheaper.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
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
        PermissionEntity adminReadPermission = findOrCreatePermission(
                "ORDER_READ",
                "Xem toàn bộ đơn hàng",
                "Xem danh sách và chi tiết toàn bộ đơn hàng phía quản trị");
        PermissionEntity adminUpdatePermission = findOrCreatePermission(
                "ORDER_UPDATE",
                "Cập nhật đơn hàng",
                "Cập nhật trạng thái đơn hàng phía quản trị");

        PermissionEntity userReadPermission = findOrCreatePermission(
                "USER_ORDER_READ",
                "Xem đơn hàng của mình",
                "Xem danh sách và chi tiết đơn hàng thuộc tài khoản hiện tại");
        PermissionEntity userCreatePermission = findOrCreatePermission(
                "USER_ORDER_CREATE",
                "Tạo đơn hàng",
                "Tạo đơn hàng từ tài khoản hiện tại");
        PermissionEntity userCancelPermission = findOrCreatePermission(
                "USER_ORDER_CANCEL",
                "Hủy đơn hàng của mình",
                "Hủy đơn hàng thuộc tài khoản hiện tại khi trạng thái cho phép");

        roleRepository.findByName(Shared.ADMIN_ROLE).ifPresent(adminRole -> {
            ensureRolePermission(adminRole, adminReadPermission);
            ensureRolePermission(adminRole, adminUpdatePermission);
        });

        roleRepository.findByName(Shared.USER_ROLE).ifPresent(userRole -> {
            ensureRolePermission(userRole, userReadPermission);
            ensureRolePermission(userRole, userCreatePermission);
            ensureRolePermission(userRole, userCancelPermission);
        });
    }

    private PermissionEntity findOrCreatePermission(String code, String name, String description) {
        return permissionRepository.findAll().stream()
                .filter(permission -> code.equals(permission.getCode()))
                .findFirst()
                .orElseGet(() -> permissionRepository.save(PermissionEntity.builder()
                        .name(name)
                        .code(code)
                        .description(description)
                        .build()));
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
