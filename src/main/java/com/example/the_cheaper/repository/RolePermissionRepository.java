package com.example.the_cheaper.repository;

import com.example.the_cheaper.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, Long> {
    boolean existsByPermissionId(Long permissionId);

    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    List<RolePermissionEntity> findAllByRoleId(Long roleId);

    Optional<RolePermissionEntity> findByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
