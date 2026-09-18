package com.example.the_cheaper.unit.security;

import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.security.CustomUserDetails;
import com.example.the_cheaper.security.CustomUserDetailsService;
import com.example.the_cheaper.security.JwtAuthenticationFilter;
import com.example.the_cheaper.security.JwtProvider;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthenticationFilter(jwtProvider, userDetailsService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validTokenForLockedAccount_DoesNotCreateAuthentication() throws Exception {
        String token = "locked-token";
        String email = "locked@example.com";
        AccountEntity account = AccountEntity.builder()
                .id(10L)
                .name("Locked")
                .email(email)
                .passwordHash("hash")
                .status(0)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(
                account,
                List.of(new SimpleGrantedAuthority("ORDER_READ")));

        MockHttpServletRequest request = requestWithToken(token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtProvider.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtProvider.isTokenValid(token, email)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void validTokenForActiveAccount_CreatesAuthentication() throws Exception {
        String token = "active-token";
        String email = "active@example.com";
        AccountEntity account = AccountEntity.builder()
                .id(11L)
                .name("Active")
                .email(email)
                .passwordHash("hash")
                .status(1)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(
                account,
                List.of(new SimpleGrantedAuthority("ORDER_READ")));

        MockHttpServletRequest request = requestWithToken(token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtProvider.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtProvider.isTokenValid(token, email)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting("authority")
                .containsExactly("ORDER_READ");
        verify(filterChain).doFilter(request, response);
    }

    private MockHttpServletRequest requestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }
}
