package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.repository.*;
import com.example.the_cheaper.security.JwtProvider;
import com.example.the_cheaper.testconfig.MySqlIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@Transactional
abstract class RbacIntegrationSupport extends MySqlIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired AccountRepository accounts;
    @Autowired RoleRepository roles;
    @Autowired PermissionRepository permissions;
    @Autowired RolePermissionRepository grants;
    @Autowired JwtProvider jwt;

    AccountEntity account(String name, String roleName) {
        RoleEntity role = roles.findByName(roleName).orElseGet(() -> roles.save(RoleEntity.builder().name(roleName).build()));
        var account = AccountEntity.builder().name(name).email(name + "@rbac-sync.example.com")
                .passwordHash("test-only").status(1).build();
        account.addRole(role);
        return accounts.saveAndFlush(account);
    }

    void grant(RoleEntity role, String code) {
        grants.saveAndFlush(RolePermissionEntity.builder().role(role)
                .permission(permissions.findByCode(code).orElseThrow()).build());
    }

    String bearer(AccountEntity account) { return "Bearer " + jwt.generateAccessToken(account); }
}
