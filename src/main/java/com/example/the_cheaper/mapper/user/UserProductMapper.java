package com.example.the_cheaper.mapper.user;

import com.example.the_cheaper.dto.response.user.UserProductOverviewResponse;
import com.example.the_cheaper.entity.ProductEntity;
import com.example.the_cheaper.entity.ProductImageEntity;
import com.example.the_cheaper.entity.ProductVariantEntity;
import com.example.the_cheaper.entity.OptionValueEntity;
import com.example.the_cheaper.dto.response.user.UserProductDetailResponse;
import com.example.the_cheaper.dto.response.user.UserVariantInfoResponse;
import com.example.the_cheaper.dto.response.user.UserImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserProductMapper {

    @Mapping(target = "brand", source = "brand.name")
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "material", source = "material.name")
    @Mapping(target = "price", source = "salePrice")
    @Mapping(target = "originalPrice", source = "comparePrice")
    @Mapping(target = "discountPercentage", expression = "java(entity.calculateDiscountPercentage())")
    @Mapping(target = "isAvailable", expression = "java(entity.isAvailable())")
    UserProductDetailResponse toDetailResponse(ProductEntity entity);

    @Mapping(target = "brand", source = "brand.name")
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "price", source = "salePrice")
    @Mapping(target = "originalPrice", source = "comparePrice")
    @Mapping(target = "discountPercentage", expression = "java(entity.calculateDiscountPercentage())")
    @Mapping(target = "thumbnailUrl", expression = "java(getThumbnailUrl(entity))")
    UserProductOverviewResponse toOverviewResponse(ProductEntity entity);

    default String getThumbnailUrl(ProductEntity entity) {
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            return entity.getImages().get(0).getName();
        }
        return null;
    }

    @Mapping(target = "price", source = "overridePrice")
    @Mapping(target = "inStock", expression = "java(entity.isInStock())")
    @Mapping(target = "attributes", source = "optionValues", qualifiedByName = "mapOptionValues")
    UserVariantInfoResponse toVariantResponse(ProductVariantEntity entity);

    @Mapping(target = "name", source = "name")
    UserImageResponse toImageResponse(ProductImageEntity entity);

    @Named("mapOptionValues")
    default Map<String, String> mapOptionValues(List<OptionValueEntity> optionValues) {
        if (optionValues == null) {
            return null;
        }
        return optionValues.stream()
                .collect(Collectors.toMap(
                        ov -> ov.getOptionAttribute() != null ? ov.getOptionAttribute().getName() : "Unknown",
                        OptionValueEntity::getValue,
                        (v1, v2) -> v1 // In case of duplicate attributes, keep the first one
                ));
    }
}


