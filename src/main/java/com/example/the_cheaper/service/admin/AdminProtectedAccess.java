package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.exception.AuthorizationException;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminProtectedAccess {

    public void adminAccess(AccountEntity currentUser) {
        if(currentUser == null) {
            throw new NotImplementedException("Cần Đang nhập để truy cập chức năng này");
        }
        String ownerRole = currentUser.getRole().getName();

        if(!ownerRole.equals(Shared.ADMIN_ROLE)) {
            throw new AuthorizationException("Bạn không có quyền truy cập chức năng này");
        }
    }
}
