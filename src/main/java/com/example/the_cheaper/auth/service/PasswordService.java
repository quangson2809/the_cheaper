package com.example.the_cheaper.auth.service;

import com.example.the_cheaper.auth.dto.request.ChangePasswordRequest;
import com.example.the_cheaper.auth.dto.request.ForgotPasswordRequest;
import com.example.the_cheaper.auth.dto.request.VerifyOtpRequest;
import com.example.the_cheaper.auth.dto.response.AuthResponse;
import com.example.the_cheaper.account.entity.AccountEntity;
import com.example.the_cheaper.auth.entity.PasswordResetTokenEntity;
import com.example.the_cheaper.common.exception.InvalidInputException;
import com.example.the_cheaper.common.exception.ResourceNotFoundException;
import com.example.the_cheaper.infrastructure.mail.EmailService;
import com.example.the_cheaper.account.repository.AccountRepository;
import com.example.the_cheaper.auth.repository.PasswordResetTokenRepository;
import com.example.the_cheaper.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final AccountRepository accountRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Value("${app.otp.expiration-minutes:15}")
    private int otpExpirationMinutes;

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        AccountEntity account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản với email: " + request.getEmail()));

        passwordResetTokenRepository.deleteByEmail(request.getEmail());

        String otp = generateOtp();

        PasswordResetTokenEntity token = PasswordResetTokenEntity.builder()
                .email(request.getEmail())
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .used(false)
                .build();
        passwordResetTokenRepository.save(token);

        System.out.println("Generated OTP for " + request.getEmail() + ": " + otp);
        return otp;
    }

    @Transactional
    public AuthResponse verifyOtpAndResetPassword(VerifyOtpRequest request) {
        PasswordResetTokenEntity token = passwordResetTokenRepository
                .findByEmailAndOtpAndUsedFalse(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new InvalidInputException("OTP không hợp lệ"));

        if (token.isExpired()) {
            passwordResetTokenRepository.delete(token);
            throw new InvalidInputException("OTP đã hết hạn, vui lòng yêu cầu OTP mới");
        }

        AccountEntity account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản với email: " + request.getEmail()));

        account.changePasswordHash(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        passwordResetTokenRepository.delete(token);

        String accessToken = jwtProvider.generateAccessToken(account);
        String refreshToken = jwtProvider.generateRefreshToken(account);

        account.setRefreshToken(refreshToken);
        accountRepository.save(account);

        String roleName = account.getAccountRoles().stream()
                .map(accountRole -> accountRole.getRole())
                .filter(role -> role != null && role.getName() != null)
                .map(role -> role.getName())
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
    public void changePassword(Long userId, ChangePasswordRequest request) {
        AccountEntity account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        if (!passwordEncoder.matches(request.getOldPassword(), account.getPasswordHash())) {
            throw new InvalidInputException("Mật khẩu cũ không chính xác");
        }

        account.changePasswordHash(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
