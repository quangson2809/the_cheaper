package com.example.the_cheaper.repository;

import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, Long> {
    boolean existsByPermissionId(Long permissionId);

    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    boolean existsByRoleId(Long roleId);

    List<RolePermissionEntity> findAllByRoleId(Long roleId);

    Optional<RolePermissionEntity> findByRoleIdAndPermissionId(Long roleId, Long permissionId);

    @Query("select rp.permission from RolePermissionEntity rp where rp.role.id = :roleId")
    List<PermissionEntity> findPermissionsByRoleId(@Param("roleId") Long roleId);
}
