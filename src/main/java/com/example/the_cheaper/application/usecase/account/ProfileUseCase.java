package com.example.the_cheaper.application.usecase.account;

import com.example.the_cheaper.application.command.UpdateProfileCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.response.user.AccountResponse;
import com.example.the_cheaper.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileUseCase {

    private final AccountRepository accountRepository;

    public ProfileUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse getProfile(Long userId) {
        throw new NotImplementedException("Chức năng xem hồ sơ chưa được triển khai");
    }

    public AccountResponse updateProfile(UpdateProfileCommand command) {
        throw new NotImplementedException("Chức năng cập nhật hồ sơ chưa được triển khai");
    }

}


