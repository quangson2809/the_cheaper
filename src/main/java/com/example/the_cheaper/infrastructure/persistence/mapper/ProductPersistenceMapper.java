package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.*;
import com.example.the_cheaper.infrastructure.persistence.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {

    @Mapping(target = "salePrice", source = "salePrice", qualifiedByName = "moneyToBigDecimal")
    @Mapping(target = "comparePrice", source = "comparePrice", qualifiedByName = "moneyToBigDecimal")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "category", source = "category")
    ProductEntity toEntity(Product product);

    @Mapping(target = "salePrice", source = "salePrice", qualifiedByName = "bigDecimalToMoney")
    @Mapping(target = "comparePrice", source = "comparePrice", qualifiedByName = "bigDecimalToMoney")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "category", source = "category")
    Product toDomain(ProductEntity entity);

    BrandEntity toEntity(Brand brand);
    Brand toDomain(BrandEntity entity);

    CategoryEntity toEntity(Category category);
    Category toDomain(CategoryEntity entity);

    ProductImageEntity toEntity(ProductImage image);
    ProductImage toDomain(ProductImageEntity entity);

    @Mapping(target = "overiteSalePrice", source = "overiteSalePrice", qualifiedByName = "moneyToBigDecimal")
    ProductVariantEntity toEntity(ProductVariant variant);

    @Mapping(target = "overiteSalePrice", source = "overiteSalePrice", qualifiedByName = "bigDecimalToMoney")
    ProductVariant toDomain(ProductVariantEntity entity);

    OptionAttributeEntity toEntity(OptionAttribute attribute);
    OptionAttribute toDomain(OptionAttributeEntity entity);

    OptionValueEntity toEntity(OptionValue value);
    OptionValue toDomain(OptionValueEntity entity);

    @Named("moneyToBigDecimal")
    default BigDecimal moneyToBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    @Named("bigDecimalToMoney")
    default Money bigDecimalToMoney(BigDecimal value) {
        return value != null ? new Money(value) : null;
    }
}
