package com.example.the_cheaper.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.the_cheaper.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);

    @Query("select count(a) > 0 from AccountEntity a where a.role.id = :roleId")
    boolean existsAssignedToAccount(@Param("roleId") Long roleId);
}
