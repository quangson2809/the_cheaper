package com.example.the_cheaper.service.auth;

import com.example.the_cheaper.dto.request.auth.LoginRequest;
import com.example.the_cheaper.dto.request.auth.RegisterRequest;
import com.example.the_cheaper.dto.response.auth.AuthResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.CartEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.security.CustomUserDetails;
import com.example.the_cheaper.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(AccountRepository accountRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtProvider jwtProvider,
                       AuthenticationManager authenticationManager) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email đã được sử dụng");
        }

        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role mặc định không tồn tại"));

        CartEntity cart =new CartEntity();

        AccountEntity account = AccountEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .cart(cart)
                .status(1)
                .rewardPoint(0)
                .build();

        cart.setAccount(account);

        AccountEntity savedAccount = accountRepository.save(account);

        String accessToken = jwtProvider.generateAccessToken(savedAccount);
        String refreshToken = jwtProvider.generateRefreshToken(savedAccount);

        account.setRefreshToken(refreshToken);
        accountRepository.save(account);

        return AuthResponse.builder()
                .id(savedAccount.getId())
                .name(savedAccount.getName())
                .email(savedAccount.getEmail())
                .role(userRole.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new InvalidInputException("Thông tin đăng nhập không chính xác hoặc tài khoản bị vô hiệu hóa");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AccountEntity account = userDetails.getAccount();

        String accessToken = jwtProvider.generateAccessToken(account);
        String refreshToken = jwtProvider.generateRefreshToken(account);
        String roleName = account.getRole() != null ? account.getRole().getName() : null;

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
    public void logout(Long userId) {
        throw new NotImplementedException("Chức năng đăng xuất chưa được triển khai đầy đủ (có thể cần Redis hoặc bảng lưu blacklist token)");
    }
}
