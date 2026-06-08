package com.example.the_cheaper.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.the_cheaper.entity.ProductEntity;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByName(String name);

    @Query("SELECT p FROM ProductEntity p WHERE " + "p.isDeleted = false " +
            "AND p.status = 1 " +
            "AND " + "(:category IS NULL OR p.category.id = :category) " +
            "AND " + "(:brand IS NULL OR p.brand.id = :brand)")
    Page<ProductEntity> findActiveProductsByCategoryAndBrand(
            @Param("category") Long category,
            @Param("brand") Long brand,
            Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE " + "p.isDeleted = false " +
            "AND p.status = 1 " +
            "AND " + "(:category IS NULL OR p.category.id = :category) " +
            "AND " + "(:brand IS NULL OR p.brand.id = :brand)" +
            "AND (:minPrice IS NULL OR p.salePrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.salePrice <= :maxPrice) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'price' THEN p.salePrice END ASC")
    Page<ProductEntity> findActiveProductsByUserFilter(
            @Param("category") Long category,
            @Param("brand") Long brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("sortBy") String sortBy,
            Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE " + "p.isDeleted = false " +
            "AND" + "(:status IS NULL OR p.status = :status) " +
            "AND " + "(:category IS NULL OR p.category.id = :category) " +
            "AND " + "(:brand IS NULL OR p.brand.id = :brand)" +
            "AND " + "(:material IS NULL OR p.material.id = :material) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'price' THEN p.salePrice END ASC")
    Page<ProductEntity> findProductsByAdminFilter(
            @Param("category") Long category,
            @Param("brand") Long brand,
            @Param("material") Long material,
            @Param("status") Integer status,
            @Param("sortBy") String sortBy,
            Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE " +
           "p.isDeleted = false AND p.status = 1 AND " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ProductEntity> findActiveProductsByNameContainingIgnoreCase(
            @Param("name") String name, 
            Pageable pageable);
}
