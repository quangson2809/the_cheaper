package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.*;
import com.example.the_cheaper.infrastructure.persistence.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CartPersistenceMapper {

    @Mapping(target = "account", source = "accountId", qualifiedByName = "idToAccountEntity")
    CartEntity toEntity(Cart cart);

    @Mapping(target = "accountId", source = "account.id")
    Cart toDomain(CartEntity entity);

    @Mapping(target = "variant", source = "variantId", qualifiedByName = "idToVariantEntity")
    CartItemEntity toEntity(CartItem item);

    @Mapping(target = "variantId", source = "variant.id")
    CartItem toDomain(CartItemEntity entity);

    @Named("idToAccountEntity")
    default AccountEntity idToAccountEntity(Long id) {
        if (id == null) return null;
        AccountEntity entity = new AccountEntity();
        entity.setId(id);
        return entity;
    }

    @Named("idToVariantEntity")
    default ProductVariantEntity idToVariantEntity(Long id) {
        if (id == null) return null;
        ProductVariantEntity entity = new ProductVariantEntity();
        entity.setId(id);
        return entity;
    }
}
