package com.example.the_cheaper.infrastructure.persistence.adapter;

import com.example.the_cheaper.domain.model.Review;
import com.example.the_cheaper.domain.repository.ReviewRepository;
import com.example.the_cheaper.infrastructure.persistence.entity.ReviewEntity;
import com.example.the_cheaper.infrastructure.persistence.mapper.ReviewPersistenceMapper;
import com.example.the_cheaper.infrastructure.persistence.repository.JpaReviewRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReviewRepositoryImpl implements ReviewRepository {

    private final JpaReviewRepository jpaReviewRepository;
    private final ReviewPersistenceMapper reviewPersistenceMapper;

    public ReviewRepositoryImpl(
            JpaReviewRepository reviewJpaRepository,
            ReviewPersistenceMapper reviewPersistenceMapper
    ) {
        this.jpaReviewRepository = reviewJpaRepository;
        this.reviewPersistenceMapper = reviewPersistenceMapper;
    }

    @Override
    public Review save(Review review) {
        ReviewEntity reviewEntity = reviewPersistenceMapper.toEntity(review);
        ReviewEntity savedEntity = jpaReviewRepository.save(reviewEntity);
        return reviewPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Review> findById(Long id) {
        return jpaReviewRepository.findById(id)
                .map(reviewPersistenceMapper::toDomain);
    }

    @Override
    public List<Review> findAll() {
        return jpaReviewRepository.findAll().stream()
                .map(reviewPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaReviewRepository.deleteById(id);
    }

    @Override
    public void delete(Review review) {
        jpaReviewRepository.delete(reviewPersistenceMapper.toEntity(review));
    }

    @Override
    public List<Review> findByProductId(Long productId) {
        return jpaReviewRepository.findByProductId(productId).stream()
                .map(reviewPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

}
