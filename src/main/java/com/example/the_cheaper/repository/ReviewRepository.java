package com.example.the_cheaper.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.the_cheaper.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByProductIdOrderByCreatedAtDesc(Long productId);

    boolean existsByAccountIdAndProductId(Long accountId, Long productId);
}
