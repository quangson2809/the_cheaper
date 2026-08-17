package com.example.the_cheaper.security;

import com.example.the_cheaper.entity.AccountEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class CustomUserDetails implements UserDetails {
    private final AccountEntity account;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(AccountEntity account, Collection<? extends GrantedAuthority> authorities) {
        this.account = account;
        this.authorities = authorities;
    }

    public AccountEntity getAccount() {
        return account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public static Collection<? extends GrantedAuthority> authorities(
            String roleName,
            List<String> permissionCodes) {
        Stream<GrantedAuthority> roleAuthority = roleName == null || roleName.isBlank()
                ? Stream.empty()
                : Stream.of(new SimpleGrantedAuthority("ROLE_" + roleName));

        Stream<GrantedAuthority> permissionAuthorities = permissionCodes == null
                ? Stream.empty()
                : permissionCodes.stream()
                .filter(code -> code != null && !code.isBlank())
                .map(SimpleGrantedAuthority::new);

        return Stream.concat(roleAuthority, permissionAuthorities).toList();
    }

    @Override
    public String getPassword() {
        return account.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return account.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return account.isActive();
    }
}
