package com.example.the_cheaper.domain.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private String material;
    private Boolean isDelete;
    private Money comparePrice;
    private Money salePrice;
    private Brand brand;
    private Category category;
    private List<ProductImage> images;
    private List<ProductVariant> variants;

    public void addVariant(ProductVariant variant) {

        variants.add(variant);
    }

    public void addImage(ProductImage image) {

        images.add(image);
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(isDelete);
    }
}
