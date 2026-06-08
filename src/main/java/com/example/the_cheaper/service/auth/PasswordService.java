package com.example.the_cheaper.service.auth;

import com.example.the_cheaper.dto.request.auth.ChangePasswordRequest;
import com.example.the_cheaper.dto.request.auth.ForgotPasswordRequest;
import com.example.the_cheaper.dto.request.auth.VerifyOtpRequest;
import com.example.the_cheaper.dto.response.auth.AuthResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.PasswordResetTokenEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.external.EmailService;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.PasswordResetTokenRepository;
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
        int otp = 100000 + random.nextInt(900000); // 6 digits: 100000–999999
        return String.valueOf(otp);
    }
}

