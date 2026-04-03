package com.example.the_cheaper.domain.repository;

import com.example.the_cheaper.domain.model.Account;
import com.example.the_cheaper.domain.model.Email;
import java.util.Optional;

public interface AccountRepository extends BaseRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);
}

