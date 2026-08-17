package com.example.the_cheaper.service.authorization;

import com.example.the_cheaper.repository.AccountRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorizationQueryService {

    private final AccountRoleRepository accountRoleRepository;

    public Set<String> findAuthorities(Long accountId) {
        Set<String> authorities = new HashSet<>();

        accountRoleRepository.findRoleNamesByAccountId(accountId)
                .stream()
                .filter(role -> role != null && !role.isBlank())
                .map(role -> "ROLE_" + role)
                .forEach(authorities::add);

        accountRoleRepository.findPermissionCodesByAccountId(accountId)
                .stream()
                .filter(code -> code != null && !code.isBlank())
                .forEach(authorities::add);

        return Set.copyOf(authorities);
    }
}
