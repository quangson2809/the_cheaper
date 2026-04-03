package com.example.the_cheaper.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {
    private Long id;
    private String name;
    private String alt;
}
