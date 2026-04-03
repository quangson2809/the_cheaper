package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.CartItem;
import com.example.the_cheaper.infrastructure.persistence.entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.example.the_cheaper.infrastructure.persistence.entity.ProductVariantEntity;

@Mapper(componentModel = "spring")
public interface CartItemPersistenceMapper {

    @Mapping(target = "variantId", source = "variant.id")
    CartItem toDomain(CartItemEntity entity);

    @Mapping(target = "variant", source = "variantId", qualifiedByName = "idToVariantEntity")
    CartItemEntity toEntity(CartItem domain);

    @Named("idToVariantEntity")
    default ProductVariantEntity idToVariantEntity(Long id) {
        if (id == null) return null;
        ProductVariantEntity entity = new ProductVariantEntity();
        entity.setId(id);
        return entity;
    }
}
