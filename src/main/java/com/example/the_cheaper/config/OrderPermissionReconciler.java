package com.example.the_cheaper.config;

import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.repository.PermissionRepository;
import com.example.the_cheaper.repository.RolePermissionRepository;
import com.example.the_cheaper.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(101)
public class OrderPermissionReconciler implements CommandLineRunner {

    private static final List<String> STAFF_ONLY_ORDER_PERMISSIONS = List.of(
            "ORDER_READ",
            "ORDER_UPDATE",
            "ORDER_CONFIRM",
            "ORDER_CANCEL",
            "ORDER_DELIVERY_UPDATE",
            "ORDER_PAYMENT_COLLECT"
    );

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        roleRepository.findByName(Shared.USER_ROLE).ifPresent(userRole ->
                STAFF_ONLY_ORDER_PERMISSIONS.forEach(code -> removePermission(userRole, code)));
    }

    private void removePermission(RoleEntity role, String permissionCode) {
        permissionRepository.findByCode(permissionCode)
                .map(PermissionEntity::getId)
                .ifPresent(permissionId ->
                        rolePermissionRepository.deleteByRoleIdAndPermissionId(role.getId(), permissionId));
    }
}
