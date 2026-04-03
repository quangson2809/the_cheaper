package com.example.the_cheaper.application.usecase.auth;

import com.example.the_cheaper.application.command.ForgotPasswordCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordUseCase {

    private final AccountRepository accountRepository;

    public ForgotPasswordUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void forgotPassword(ForgotPasswordCommand command) {
        throw new NotImplementedException("Chức năng quên mật khẩu chưa được triển khai");
    }

}


