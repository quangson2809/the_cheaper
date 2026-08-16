package com.example.the_cheaper.mapper.admin;

import com.example.the_cheaper.dto.request.admin.AdminProductCreateRequest;
import com.example.the_cheaper.dto.response.admin.*;
import com.example.the_cheaper.entity.OptionValueEntity;
import com.example.the_cheaper.entity.ProductEntity;
import com.example.the_cheaper.entity.ProductImageEntity;
import com.example.the_cheaper.entity.ProductVariantEntity;
import com.example.the_cheaper.dto.request.admin.AdminVariantCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AdminBrandMapper.class, AdminCategoryMapper.class, AdminMaterialMapper.class})
public interface AdminProductMapper {
    AdminProductResponse toDetailResponse(ProductEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "material", ignore = true)
    @Mapping(target = "variants", ignore = true)
    ProductEntity toEntity(AdminProductCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductVariantEntity toEntity(String sku,AdminVariantCreateRequest request, List<OptionValueEntity> optionValues);

    @Mapping(target = "thumbnailUrl", expression = "java(getThumbnailUrl(entity))")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "totalStock", expression = "java(entity.getTotalStock())")
    @Mapping(target = "totalSold",expression = "java(entity.getTotalSold())")
    AdminProductOverviewResponse toOverviewResponse(ProductEntity entity);

    AdminVariantResponse toDetailResponse(ProductVariantEntity entity);

    @Mapping(target = "attributeName", source = "optionAttribute.name")
    AdminOptionValueResponse toDetailResponse(com.example.the_cheaper.entity.OptionValueEntity entity);

    AdminProductImageResponse toDetailResponse(ProductImageEntity entity);

    default String getThumbnailUrl(ProductEntity entity) {
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            return entity.getImages().get(0).getName();
        }
        return null;
    }
}


