package com.example.the_cheaper.interfaces.rest.mapper.user;

import com.example.the_cheaper.application.command.UpdateProfileCommand;
import com.example.the_cheaper.application.query.SearchQuery;
import com.example.the_cheaper.interfaces.rest.dto.request.common.SearchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "userId", source = "userId")
    UpdateProfileCommand toUpdateCommand(Long userId, Object request); // Using Object for now as ProfileUseCase uses it

    SearchQuery toQuery(SearchRequest request);
}
