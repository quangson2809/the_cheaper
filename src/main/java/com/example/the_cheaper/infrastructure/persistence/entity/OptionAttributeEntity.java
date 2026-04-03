package com.example.the_cheaper.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "option_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionAttributeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "optionAttribute", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OptionValueEntity> values = new ArrayList<>();
}
