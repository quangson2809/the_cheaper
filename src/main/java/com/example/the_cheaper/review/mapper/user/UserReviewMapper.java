package com.example.the_cheaper.review.mapper.user;

import com.example.the_cheaper.review.dto.response.user.UserReviewResponse;
import com.example.the_cheaper.review.entity.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserReviewMapper {

    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "productId", source = "product.id")
    UserReviewResponse toResponse(ReviewEntity entity);
}
