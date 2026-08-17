package com.example.the_cheaper.security;

import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.service.authorization.AuthorizationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final AuthorizationQueryService authorizationQueryService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        AccountEntity account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy email: " + email));

        var authorities = authorizationQueryService
                .findAuthorities(account.getId())
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new CustomUserDetails(account, authorities);
    }
}
