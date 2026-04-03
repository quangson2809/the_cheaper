package com.example.the_cheaper.application.usecase.account;

import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.UserResponse;
import com.example.the_cheaper.domain.repository.AccountRepository;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class ManageAccountUseCase {

    private final AccountRepository accountRepository;

    public ManageAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void deleteAccount(Long id) {
        throw new NotImplementedException("Chức năng xóa tài khoản chưa được triển khai");
    }

    public List<UserResponse> listAccounts() {
        throw new NotImplementedException("Chức năng liệt kê tài khoản chưa được triển khai");
    }
}
