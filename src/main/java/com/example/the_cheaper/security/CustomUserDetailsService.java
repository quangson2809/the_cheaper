package com.example.the_cheaper.security;

import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.RolePermissionRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public CustomUserDetailsService(
            AccountRepository accountRepository,
            RolePermissionRepository rolePermissionRepository) {
        this.accountRepository = accountRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AccountEntity account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy email: " + email));

        Long roleId = account.getRole() != null ? account.getRole().getId() : null;
        List<String> permissionCodes = roleId == null
                ? List.of()
                : rolePermissionRepository.findPermissionsByRoleId(roleId).stream()
                .map(PermissionEntity::getCode)
                .toList();

        String roleName = account.getRole() != null ? account.getRole().getName() : null;
        return new CustomUserDetails(account, CustomUserDetails.authorities(roleName, permissionCodes));
    }
}
