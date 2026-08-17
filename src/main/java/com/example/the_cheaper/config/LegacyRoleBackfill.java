package com.example.the_cheaper.config;

import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.AccountRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class LegacyRoleBackfill implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        int migrated = 0;

        for (AccountEntity account : accountRepository.findAll()) {
            if (account.getRole() == null
                    || !accountRoleRepository.findAllByAccountId(account.getId()).isEmpty()) {
                continue;
            }

            accountRoleRepository.save(
                    com.example.the_cheaper.entity.AccountRoleEntity.builder()
                            .account(account)
                            .role(account.getRole())
                            .build()
            );
            migrated++;
        }

        if (migrated > 0) {
            log.info("Backfilled {} legacy account role assignments into account_roles", migrated);
        }
    }
}
