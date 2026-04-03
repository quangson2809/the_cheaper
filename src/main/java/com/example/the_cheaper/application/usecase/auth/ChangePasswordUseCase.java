package com.example.the_cheaper.application.usecase.auth;

import com.example.the_cheaper.application.command.ChangePasswordCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordUseCase {

    private final AccountRepository accountRepository;

    public ChangePasswordUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void changePassword(ChangePasswordCommand command) {
        throw new NotImplementedException("Chức năng đổi mật khẩu chưa được triển khai");
    }
}
