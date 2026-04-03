package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.Review;
import com.example.the_cheaper.infrastructure.persistence.entity.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewPersistenceMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "productId", source = "product.id")
    Review toDomain(ReviewEntity entity);

    @Mapping(target = "account.id", source = "accountId")
    @Mapping(target = "product.id", source = "productId")
    ReviewEntity toEntity(Review domain);
}
