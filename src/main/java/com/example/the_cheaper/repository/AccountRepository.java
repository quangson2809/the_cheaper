package com.example.the_cheaper.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.the_cheaper.entity.AccountEntity;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    @EntityGraph(attributePaths = "role")
    Optional<AccountEntity> findByEmail(String email);

    boolean existsByEmail(String email);

//    @Query("SELECT p FROM AccountEntity p WHERE " + "(:status IS NULL OR p.status = :status) " +
//            "AND " + "(:role IS NULL OR :role = '' OR p.role.name = :role) ")
    Page<AccountEntity> findAllBy(
            @Param("status") Integer status,
            @Param("role") String role,
            Pageable pageable
    );
}
