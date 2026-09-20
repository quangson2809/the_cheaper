package com.example.the_cheaper.catalog.mapper.admin;


import com.example.the_cheaper.catalog.dto.response.admin.AdminOptionValueResponse;
import com.example.the_cheaper.catalog.dto.response.admin.AdminProductImageResponse;
import com.example.the_cheaper.catalog.dto.response.admin.AdminProductOverviewResponse;
import com.example.the_cheaper.catalog.dto.response.admin.AdminProductResponse;
import com.example.the_cheaper.catalog.dto.response.admin.AdminVariantResponse;
import com.example.the_cheaper.catalog.dto.request.admin.AdminProductCreateRequest;
import com.example.the_cheaper.catalog.entity.OptionValueEntity;
import com.example.the_cheaper.catalog.entity.ProductEntity;
import com.example.the_cheaper.catalog.entity.ProductImageEntity;
import com.example.the_cheaper.catalog.entity.ProductVariantEntity;
import com.example.the_cheaper.catalog.dto.request.admin.AdminVariantCreateRequest;
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
    AdminOptionValueResponse toDetailResponse(com.example.the_cheaper.catalog.entity.OptionValueEntity entity);

    AdminProductImageResponse toDetailResponse(ProductImageEntity entity);

    default String getThumbnailUrl(ProductEntity entity) {
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            return entity.getImages().get(0).getName();
        }
        return null;
    }
}


