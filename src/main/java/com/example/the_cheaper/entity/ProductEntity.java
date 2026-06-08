package com.example.the_cheaper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private MaterialEntity material;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "status")
    @Builder.Default
    private int status = 1;

    @Column(name = "compare_price", precision = 19, scale = 2)
    private BigDecimal comparePrice;

    @Column(name = "sale_price", precision = 19, scale = 2)
    private BigDecimal salePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "product")
    @Builder.Default
    private List<ProductImageEntity> images = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @Builder.Default
    private List<ProductVariantEntity> variants = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public Integer calculateDiscountPercentage() {
        if (this.comparePrice == null || this.salePrice == null || this.comparePrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        if (this.salePrice.compareTo(this.comparePrice) >= 0) {
            return 0;
        }
        return this.comparePrice.subtract(this.salePrice)
                .divide(this.comparePrice, 2, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .intValue();
    }

    public boolean isAvailable() {
        return status == 1;
    }

    public String getThumbnail() {
        if (this.images != null && !this.images.isEmpty()) {
            return this.images.get(0).getName();
        }
        return null; // or return a default image URL
    }

    public int getTotalStock() {
        if (this.variants == null || this.variants.isEmpty()) {
            return 0;
        }
        return this.variants.stream().mapToInt(ProductVariantEntity::getStock).sum();
    }

    public int getTotalSold() {
        if (this.variants == null || this.variants.isEmpty()) {
            return 0;
        }
        return this.variants.stream().mapToInt(ProductVariantEntity::getSold).sum();
    }
}
