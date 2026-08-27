package com.example.the_cheaper.service.auth;

import com.example.the_cheaper.dto.request.auth.LoginRequest;
import com.example.the_cheaper.dto.request.auth.RegisterRequest;
import com.example.the_cheaper.dto.response.auth.AuthResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.CartEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.security.CustomUserDetails;
import com.example.the_cheaper.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email đã được sử dụng");
        }

        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role mặc định không tồn tại"));

        CartEntity cart = new CartEntity();

        AccountEntity account = AccountEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .cart(cart)
                .status(1)
                .rewardPoint(0)
                .build();

        account.addRole(userRole);
        cart.setAccount(account);

        AccountEntity savedAccount = accountRepository.save(account);

        String accessToken = jwtProvider.generateAccessToken(savedAccount);
        String refreshToken = jwtProvider.generateRefreshToken(savedAccount);

        savedAccount.setRefreshToken(refreshToken);
        accountRepository.save(savedAccount);

        return AuthResponse.builder()
                .id(savedAccount.getId())
                .name(savedAccount.getName())
                .email(savedAccount.getEmail())
                .role(userRole.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new InvalidInputException(
                    "Thông tin đăng nhập không chính xác hoặc tài khoản bị vô hiệu hóa");
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        AccountEntity account = userDetails.getAccount();

        String accessToken = jwtProvider.generateAccessToken(account);
        String refreshToken = jwtProvider.generateRefreshToken(account);
        account.setRefreshToken(refreshToken);
        accountRepository.save(account);

        String roleName = account.getAccountRoles().stream()
                .map(accountRole -> accountRole.getRole().getName())
                .findFirst()
                .orElse(null);

        return AuthResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .role(roleName)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void logout(Long accountId) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tài khoản không tồn tại"));

        account.setRefreshToken(null);
        accountRepository.save(account);
    }
}
