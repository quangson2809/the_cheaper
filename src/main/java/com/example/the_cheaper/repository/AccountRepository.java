package com.example.the_cheaper.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.the_cheaper.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @EntityGraph(attributePaths = "role")
    Optional<AccountEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT a FROM AccountEntity a
            WHERE (:status IS NULL OR a.status = :status)
              AND (:role IS NULL OR :role = '' OR EXISTS (
                    SELECT ar.id
                    FROM AccountRoleEntity ar
                    WHERE ar.account.id = a.id
                      AND ar.role.name = :role
              ))
            """)
    Page<AccountEntity> findAllBy(
            @Param("status") Integer status,
            @Param("role") String role,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM AccountEntity a
            WHERE LOWER(a.phone) LIKE LOWER(CONCAT('%', :phone, '%'))
            """)
    Page<AccountEntity> findActiveAccountByPhoneContainingIgnoreCase(
            @Param("phone") String phone,
            Pageable pageable);

    @Query("""
            SELECT count(a)
            FROM AccountEntity a
            WHERE a.status = 1
              AND a.createdAt BETWEEN :from AND :to
            """)
    int countByCreatedBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
