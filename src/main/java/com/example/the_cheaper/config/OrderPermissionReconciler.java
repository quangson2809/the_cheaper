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
@Order(101)
public class OrderPermissionReconciler implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        roleRepository.findByName(Shared.USER_ROLE).ifPresent(userRole -> {
            removePermission(userRole, "ORDER_READ");
            removePermission(userRole, "ORDER_UPDATE");
        });
    }

    private void removePermission(RoleEntity role, String permissionCode) {
        permissionRepository.findByCode(permissionCode)
                .map(PermissionEntity::getId)
                .ifPresent(permissionId ->
                        rolePermissionRepository.deleteByRoleIdAndPermissionId(role.getId(), permissionId));
    }
}
