package com.example.the_cheaper.integration.security;

import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.repository.*;
import com.example.the_cheaper.security.JwtProvider;
import com.example.the_cheaper.testconfig.MySqlIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.List;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/** Real independent HTTP transactions, no enclosing test transaction. */
@AutoConfigureMockMvc
class LastAdminConcurrencyIntegrationTest extends MySqlIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired PlatformTransactionManager transactions;
    @Autowired AccountRepository accounts;
    @Autowired RoleRepository roles;
    @Autowired RolePermissionRepository grants;
    @Autowired PermissionRepository permissions;
    @Autowired JwtProvider jwt;

    @Test void simultaneousDeactivationsCannotRemoveBothActiveAdmins() throws Exception {
        var tx = new TransactionTemplate(transactions);
        var fixture = tx.execute(s -> {
            var adminRole = roles.save(RoleEntity.builder().name("ADMIN").build());
            var operatorRole = roles.save(RoleEntity.builder().name("SYNC_ACCOUNT_OPERATOR").build());
            grants.save(RolePermissionEntity.builder().role(operatorRole)
                    .permission(permissions.findByCode("ACCOUNT_STATUS_UPDATE").orElseThrow()).build());
            return List.of(account("race-admin-a", adminRole), account("race-admin-b", adminRole), account("race-operator", operatorRole));
        });
        var pool = Executors.newFixedThreadPool(2);
        try {
            String token = "Bearer " + jwt.generateAccessToken(fixture.get(2));
            var barrier = new CyclicBarrier(2);
            Callable<Integer> a = () -> disableAfterBarrier(fixture.get(0).getId(), token, barrier);
            Callable<Integer> b = () -> disableAfterBarrier(fixture.get(1).getId(), token, barrier);
            var first = pool.submit(a); var second = pool.submit(b);
            assertThat(List.of(first.get(30, TimeUnit.SECONDS), second.get(30, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(200, 400);
            assertThat(accounts.findAllById(List.of(fixture.get(0).getId(), fixture.get(1).getId())))
                    .filteredOn(AccountEntity::isActive).hasSize(1);
        } finally {
            pool.shutdownNow(); assertThat(pool.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
            tx.executeWithoutResult(s -> {
                var roleIds = fixture.stream().flatMap(a -> a.getAccountRoles().stream()).map(ar -> ar.getRole().getId()).distinct().toList();
                accounts.deleteAllById(fixture.stream().map(AccountEntity::getId).toList()); accounts.flush();
                for (Long roleId : roleIds) grants.deleteAll(grants.findAllByRoleId(roleId));
                grants.flush(); roles.deleteAllById(roleIds);
            });
        }
    }

    private int disableAfterBarrier(Long id, String token, CyclicBarrier barrier) throws Exception {
        barrier.await(15, TimeUnit.SECONDS);
        return mvc.perform(put("/api/admin/accounts/{id}/status?status=0", id).header("Authorization", token))
                .andReturn().getResponse().getStatus();
    }

    private AccountEntity account(String name, RoleEntity role) {
        var account = AccountEntity.builder().name(name).email(name + "@rbac-sync.example.com").passwordHash("test").status(1).build();
        account.addRole(role);
        return accounts.saveAndFlush(account);
    }
}
