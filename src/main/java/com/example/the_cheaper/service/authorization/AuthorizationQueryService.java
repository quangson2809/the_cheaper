package com.example.the_cheaper.service.authorization;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.response.auth.AuthoritiesResponse;
import com.example.the_cheaper.entity.PermissionEntity;
import com.example.the_cheaper.repository.AccountRoleRepository;
import com.example.the_cheaper.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorizationQueryService {

    private final AccountRoleRepository accountRoleRepository;
    private final PermissionRepository permissionRepository;

    public AuthoritiesResponse findEffectiveAuthorities(Long accountId) {
        var roles = accountRoleRepository.findRoleNamesByAccountId(accountId).stream()
                .filter(role -> role != null && !role.isBlank())
                .map(role -> Shared.ADMIN_ROLE.equalsIgnoreCase(role) ? Shared.ADMIN_ROLE : role)
                .distinct().sorted().toList();
        var roleAuthorities = roles.stream().map(role -> "ROLE_" + role).collect(java.util.stream.Collectors.toSet());
        // ROLE_PERMISSION_* are permission codes, not role authorities. Do not filter by prefix.
        var permissions = findAuthorities(accountId).stream()
                .filter(authority -> !roleAuthorities.contains(authority)).sorted().toList();
        return new AuthoritiesResponse(roles, permissions);
    }

    public Set<String> findAuthorities(Long accountId) {
        Set<String> authorities = new HashSet<>();

        List<String> roleNames = accountRoleRepository.findRoleNamesByAccountId(accountId)
                .stream()
                .filter(role -> role != null && !role.isBlank())
                .toList();

        roleNames.stream()
                .map(role -> "ROLE_" + (Shared.ADMIN_ROLE.equalsIgnoreCase(role) ? Shared.ADMIN_ROLE : role))
                .forEach(authorities::add);

        boolean isAdmin = roleNames.stream()
                .anyMatch(role -> Shared.ADMIN_ROLE.equalsIgnoreCase(role));

        if (isAdmin) {
            permissionRepository.findAll().stream()
                    .map(PermissionEntity::getCode)
                    .filter(code -> code != null && !code.isBlank())
                    .forEach(authorities::add);
        } else {
            accountRoleRepository.findPermissionCodesByAccountId(accountId)
                    .stream()
                    .filter(code -> code != null && !code.isBlank())
                    .forEach(authorities::add);
        }

        return Set.copyOf(authorities);
    }
}
