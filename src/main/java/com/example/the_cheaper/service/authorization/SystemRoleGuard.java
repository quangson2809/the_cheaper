package com.example.the_cheaper.service.authorization;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemRoleGuard {
    private final RoleRepository roles;
    private final AccountRepository accounts;

    public static boolean isSystemRole(String name) {
        return Shared.ADMIN_ROLE.equalsIgnoreCase(name) || Shared.USER_ROLE.equalsIgnoreCase(name);
    }

    /** Serialize destructive account operations, including two simultaneous admin deactivations. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void requireAnotherActiveAdmin(Long accountId) {
        roles.lockByName(Shared.ADMIN_ROLE).ifPresent(role -> {
            // A locking read sees committed state even under MySQL REPEATABLE READ.
            var activeAdmins = accounts.lockActiveAccountsByRole(role.getId());
            if (activeAdmins.size() == 1 && activeAdmins.get(0).getId().equals(accountId)) {
                throw new InvalidInputException("Không thể vô hiệu hóa hoặc xóa quản trị viên hoạt động cuối cùng");
            }
        });
    }
}
