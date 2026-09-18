package com.example.the_cheaper.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import com.example.the_cheaper.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RoleEntity r where upper(r.name) = upper(:name)")
    Optional<RoleEntity> lockByName(@Param("name") String name);

    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);
}
