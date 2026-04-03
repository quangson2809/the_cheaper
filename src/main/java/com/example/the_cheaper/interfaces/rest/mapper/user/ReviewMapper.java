package com.example.the_cheaper.interfaces.rest.mapper.user;

import com.example.the_cheaper.application.command.CreateReviewCommand;
import com.example.the_cheaper.interfaces.rest.dto.request.user.ReviewRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "productId", source = "productId")
    CreateReviewCommand toCommand(Long userId, Long productId, ReviewRequest request);
}
