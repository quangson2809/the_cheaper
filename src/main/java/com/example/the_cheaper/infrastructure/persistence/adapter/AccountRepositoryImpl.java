package com.example.the_cheaper.infrastructure.persistence.adapter;

import com.example.the_cheaper.domain.model.Account;
import com.example.the_cheaper.domain.repository.AccountRepository;
import com.example.the_cheaper.infrastructure.persistence.entity.AccountEntity;
import com.example.the_cheaper.infrastructure.persistence.mapper.AccountPersistenceMapper;
import com.example.the_cheaper.infrastructure.persistence.repository.JpaAccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AccountRepositoryImpl implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;
    private final AccountPersistenceMapper accountPersistenceMapper;

    public AccountRepositoryImpl(
            JpaAccountRepository jpaAccountRepository,
            AccountPersistenceMapper accountPersistenceMapper
    ) {
        this.jpaAccountRepository = jpaAccountRepository;
        this.accountPersistenceMapper = accountPersistenceMapper;
    }

    @Override
    public Account save(Account account) {
        AccountEntity accountEntity = accountPersistenceMapper.toEntity(account);
        AccountEntity savedEntity = jpaAccountRepository.save(accountEntity);
        return accountPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return jpaAccountRepository.findById(id)
                .map(accountPersistenceMapper::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return jpaAccountRepository.findAll().stream()
                .map(accountPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaAccountRepository.deleteById(id);
    }

    @Override
    public void delete(Account account) {
        jpaAccountRepository.delete(accountPersistenceMapper.toEntity(account));
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return jpaAccountRepository.findByEmail(email)
                .map(accountPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaAccountRepository.existsByEmail(email);
    }

}

