package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.repository.AccountRepository;
import org.springframework.stereotype.Component;

@Component
public class AdminProtectedAccess {
    private final AccountRepository accountRepository;

    public AdminProtectedAccess(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    };

    public void adminAccess(AccountEntity currentUser) {
        if(currentUser == null) {
            throw new NotImplementedException("Tài khoản không tồn tại");
        }
        String ownerRole = currentUser.getRole().getName();

        if(!ownerRole.equals(Shared.ADMIN_ROLE)) {
            throw new NotImplementedException("Bạn không có quyền truy cập chức năng này");
        }
    }
}
