package com.example.the_cheaper.application.usecase.auth;

import com.example.the_cheaper.application.command.LoginCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.response.auth.AuthResponse;
import com.example.the_cheaper.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final AccountRepository accountRepository;

    public LoginUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AuthResponse login(LoginCommand command) {
        throw new NotImplementedException("Chức năng đăng nhập chưa được triển khai");
    }
}
