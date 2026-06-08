package com.example.the_cheaper.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, CustomUserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/api/auth");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("URI = " + request.getRequestURI());
        log.debug("URI = {}", request.getRequestURI());

        String authHeader = request.getHeader("Authorization");

        // Nếu không có header hoặc không phải Bearer token → bỏ qua
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(BEARER_PREFIX.length());

        // Trích xuất email từ token
        String email;
        try {
            email = jwtProvider.extractEmail(jwt);
        } catch (Exception e) {
            log.debug("Failed to extract email from JWT: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (email == null) {
            log.debug("Email extracted from JWT is null");
            filterChain.doFilter(request, response);
            return;
        }

        // Nếu đã có Authentication rồi → không cần làm lại
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Tải thông tin user từ email
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(email);
        } catch (Exception e) {
            log.debug("Failed to load user by email: {}: {}", email, e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // Kiểm tra token hợp lệ với username của user
        boolean isValid;
        try {
            isValid = jwtProvider.isTokenValid(jwt, userDetails.getUsername());
        } catch (Exception e) {
            log.debug("JWT validation failed for user: {}: {}", email, e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (!isValid) {
            log.debug("JWT is not valid for user: {}", email);
            filterChain.doFilter(request, response);
            return;
        }

        // Tạo Authentication và đặt vào SecurityContext
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}