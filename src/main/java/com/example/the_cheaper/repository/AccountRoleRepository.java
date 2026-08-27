package com.example.the_cheaper.repository;

import com.example.the_cheaper.entity.AccountRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRoleRepository extends JpaRepository<AccountRoleEntity, Long> {

    List<AccountRoleEntity> findAllByAccountId(Long accountId);

    Optional<AccountRoleEntity> findByAccountIdAndRoleId(Long accountId, Long roleId);

    boolean existsByAccountIdAndRoleId(Long accountId, Long roleId);

    boolean existsByRoleId(Long roleId);

    @Modifying
    @Query("delete from AccountRoleEntity ar where ar.account.id = :accountId")
    void deleteAllByAccountId(@Param("accountId") Long accountId);

    @Query("""
            select distinct ar.role.name
            from AccountRoleEntity ar
            where ar.account.id = :accountId
              and ar.role.name is not null
            """)
    List<String> findRoleNamesByAccountId(@Param("accountId") Long accountId);

    @Query("""
            select distinct rp.permission.code
            from AccountRoleEntity ar
            join RolePermissionEntity rp on rp.role.id = ar.role.id
            where ar.account.id = :accountId
              and rp.permission.code is not null
            """)
    List<String> findPermissionCodesByAccountId(@Param("accountId") Long accountId);
}
