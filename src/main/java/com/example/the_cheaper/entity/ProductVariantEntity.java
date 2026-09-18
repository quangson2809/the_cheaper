package com.example.the_cheaper.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Version
    @Column(nullable = false)
    private Long version;

    private int stock;

    private int sold;

    @Column(name = "overite_sale_price", precision = 19, scale = 2)
    private BigDecimal overridePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "variant_option_values", joinColumns = @JoinColumn(name = "variant_id"), inverseJoinColumns = @JoinColumn(name = "option_value_id"))
    @Builder.Default
    private List<OptionValueEntity> optionValues = new ArrayList<>();

    public boolean isInStock() {
        return this.stock > 0;
    }

    public BigDecimal getOverridePrice() {
        return this.overridePrice != null ? this.overridePrice : product.getSalePrice();
    }

}
