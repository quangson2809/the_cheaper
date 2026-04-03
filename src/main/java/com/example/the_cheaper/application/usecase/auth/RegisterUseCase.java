package com.example.the_cheaper.application.usecase.auth;

import com.example.the_cheaper.application.command.RegisterUserCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.response.auth.AuthResponse;
import com.example.the_cheaper.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterUseCase {

    private final AccountRepository accountRepository;

    public RegisterUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AuthResponse register(RegisterUserCommand command) {
        throw new NotImplementedException("Chức năng đăng ký chưa được triển khai");
    }

    public AuthResponse registerAdmin(RegisterUserCommand command) {
        throw new NotImplementedException("Chức năng đăng ký admin chưa được triển khai");
    }
}
