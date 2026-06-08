package com.example.the_cheaper.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "option_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_attribute_id")
    private OptionAttributeEntity optionAttribute;

    @ManyToMany(mappedBy = "optionValues")
    @Builder.Default
    private List<ProductVariantEntity> variants = new ArrayList<>();
}
