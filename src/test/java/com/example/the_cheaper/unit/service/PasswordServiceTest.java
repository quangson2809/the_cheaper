package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.dto.request.auth.ChangePasswordRequest;
import com.example.the_cheaper.dto.request.auth.ForgotPasswordRequest;
import com.example.the_cheaper.dto.request.auth.VerifyOtpRequest;
import com.example.the_cheaper.dto.response.auth.AuthResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.PasswordResetTokenEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.external.EmailService;
import com.example.the_cheaper.fixtures.AccountFixtures;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.PasswordResetTokenRepository;
import com.example.the_cheaper.security.JwtProvider;
import com.example.the_cheaper.service.auth.PasswordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private PasswordService passwordService;

    @Test
    @DisplayName("forgotPassword - Should throw ResourceNotFoundException when email not found")
    void forgotPassword_ShouldThrowException_WhenEmailNotFound() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest("notfound@test.com");
        when(accountRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> passwordService.forgotPassword(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Không tìm thấy tài khoản với email");
    }

    @Test
    @DisplayName("forgotPassword - Should generate and return OTP when email exists")
    void forgotPassword_ShouldReturnOtp_WhenEmailExists() {
        // Arrange
        ReflectionTestUtils.setField(passwordService, "otpExpirationMinutes", 15);
        ForgotPasswordRequest request = new ForgotPasswordRequest("user@test.com");
        AccountEntity account = AccountFixtures.createActiveUserAccount();
        
        when(accountRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(account));
        
        // Act
        String otp = passwordService.forgotPassword(request);

        // Assert
        assertThat(otp).isNotBlank();
        assertThat(otp).hasSize(6);
        assertThat(otp).matches("\\d{6}");
        
        verify(passwordResetTokenRepository).deleteByEmail(request.getEmail());
        verify(passwordResetTokenRepository).save(any(PasswordResetTokenEntity.class));
    }

    @Test
    @DisplayName("verifyOtpAndResetPassword - Should throw InvalidInputException on invalid OTP")
    void verifyOtpAndResetPassword_ShouldThrowException_OnInvalidOtp() {
        // Arrange
        VerifyOtpRequest request = new VerifyOtpRequest("user@test.com", "123456", "newPassword");
        when(passwordResetTokenRepository.findByEmailAndOtpAndUsedFalse(request.getEmail(), request.getOtp()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> passwordService.verifyOtpAndResetPassword(request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("OTP không hợp lệ");
    }

    @Test
    @DisplayName("verifyOtpAndResetPassword - Should process successful reset")
    void verifyOtpAndResetPassword_ShouldReturnAuthResponse_OnSuccess() {
        // Arrange
        VerifyOtpRequest request = new VerifyOtpRequest("user@test.com", "123456", "newPassword");
        AccountEntity account = AccountFixtures.createActiveUserAccount();
        
        PasswordResetTokenEntity token = PasswordResetTokenEntity.builder()
                .email(request.getEmail())
                .otp(request.getOtp())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();
                
        when(passwordResetTokenRepository.findByEmailAndOtpAndUsedFalse(request.getEmail(), request.getOtp()))
                .thenReturn(Optional.of(token));
        when(accountRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(account));
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("new_encoded_pwd");
        when(jwtProvider.generateAccessToken(account)).thenReturn("access_token");
        when(jwtProvider.generateRefreshToken(account)).thenReturn("refresh_token");

        // Act
        AuthResponse response = passwordService.verifyOtpAndResetPassword(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(account.getPasswordHash()).isEqualTo("new_encoded_pwd");
        
        verify(accountRepository, times(2)).save(account);
        verify(passwordResetTokenRepository).delete(token);
    }

    @Test
    @DisplayName("changePassword - Should throw InvalidInputException if old password incorrect")
    void changePassword_ShouldThrowException_IfOldPasswordIncorrect() {
        // Arrange
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("wrong_old", "new_pwd");
        AccountEntity account = AccountFixtures.createActiveUserAccount();
        
        when(accountRepository.findById(userId)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(request.getOldPassword(), account.getPasswordHash())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> passwordService.changePassword(userId, request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Mật khẩu cũ không chính xác");
    }

    @Test
    @DisplayName("changePassword - Should update password if old password correct")
    void changePassword_ShouldUpdatePassword_IfOldPasswordCorrect() {
        // Arrange
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("correct_old", "new_pwd");
        AccountEntity account = AccountFixtures.createActiveUserAccount();
        
        when(accountRepository.findById(userId)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(request.getOldPassword(), account.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("new_encoded_pwd");

        // Act
        passwordService.changePassword(userId, request);

        // Assert
        assertThat(account.getPasswordHash()).isEqualTo("new_encoded_pwd");
        verify(accountRepository).save(account);
    }
}
