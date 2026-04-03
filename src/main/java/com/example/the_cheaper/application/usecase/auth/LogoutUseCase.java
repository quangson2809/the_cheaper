package com.example.the_cheaper.application.usecase.auth;

import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCase {

    private final AccountRepository accountRepository;

    public LogoutUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void logout(Long id) {
        throw new NotImplementedException("Chức năng đăng xuất chưa được triển khai");
    }
}
