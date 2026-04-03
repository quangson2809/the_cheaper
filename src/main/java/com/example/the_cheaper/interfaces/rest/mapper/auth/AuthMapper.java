package com.example.the_cheaper.interfaces.rest.mapper.auth;

import com.example.the_cheaper.application.command.ChangePasswordCommand;
import com.example.the_cheaper.application.command.ForgotPasswordCommand;
import com.example.the_cheaper.application.command.LoginCommand;
import com.example.the_cheaper.application.command.RegisterUserCommand;
import com.example.the_cheaper.interfaces.rest.dto.request.auth.ChangePasswordRequest;
import com.example.the_cheaper.interfaces.rest.dto.request.auth.ForgotPasswordRequest;
import com.example.the_cheaper.interfaces.rest.dto.request.auth.LoginRequest;
import com.example.the_cheaper.interfaces.rest.dto.request.auth.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    LoginCommand toCommand(LoginRequest request);

    RegisterUserCommand toCommand(RegisterRequest request);

    ForgotPasswordCommand toCommand(ForgotPasswordRequest request);

    @Mapping(target = "id", source = "userId")
    ChangePasswordCommand toCommand(Long userId, ChangePasswordRequest request);
}
