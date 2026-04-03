package com.example.the_cheaper.infrastructure.persistence.repository;

import com.example.the_cheaper.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByAccountId(Long accountId);
}
