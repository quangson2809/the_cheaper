package com.example.the_cheaper.domain.repository;

import java.util.List;

import com.example.the_cheaper.domain.model.Review;

public interface ReviewRepository extends BaseRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
}
