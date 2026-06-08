package com.example.the_cheaper.integration.service;

import com.example.the_cheaper.dto.request.auth.ChangePasswordRequest;
import com.example.the_cheaper.dto.request.auth.ForgotPasswordRequest;
import com.example.the_cheaper.dto.request.auth.VerifyOtpRequest;
import com.example.the_cheaper.dto.response.auth.AuthResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.PasswordResetTokenEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.external.EmailService;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.PasswordResetTokenRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.service.auth.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@Transactional
class PasswordServiceIntegrationTest {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private EmailService emailService;

    private AccountEntity testAccount;

    @BeforeEach
    void setUp() {
        // Setup mock for EmailService to avoid sending actual emails during integration tests
        doNothing().when(emailService).sendOtpEmail(anyString(), anyString());

        RoleEntity userRole = roleRepository.findByName("USER").orElseGet(() -> {
            RoleEntity role = new RoleEntity();
            role.setName("USER");
            return roleRepository.save(role);
        });

        AccountEntity account = AccountEntity.builder()
                .name("Password Test User")
                .email("pwdtest@example.com")
                .passwordHash(passwordEncoder.encode("oldPassword123"))
                .role(userRole)
                .status(1)
                .build();
                
        testAccount = accountRepository.save(account);
    }

    @Test
    @DisplayName("forgotPassword - Should save OTP to database")
    void forgotPassword_ShouldSaveOtpToDatabase() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest(testAccount.getEmail());

        // Act
        String otp = passwordService.forgotPassword(request);

        // Assert
        assertThat(otp).isNotBlank();
        
        List<PasswordResetTokenEntity> tokens = passwordResetTokenRepository.findAll();
        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0).getEmail()).isEqualTo(testAccount.getEmail());
        assertThat(tokens.get(0).getOtp()).isEqualTo(otp);
        assertThat(tokens.get(0).isUsed()).isFalse();
    }

    @Test
    @DisplayName("verifyOtpAndResetPassword - Should reset password successfully")
    void verifyOtpAndResetPassword_ShouldResetSuccessfully() {
        // Arrange
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest(testAccount.getEmail());
        String otp = passwordService.forgotPassword(forgotRequest);

        VerifyOtpRequest verifyRequest = new VerifyOtpRequest(testAccount.getEmail(), otp, "newPassword456");

        // Act
        AuthResponse response = passwordService.verifyOtpAndResetPassword(verifyRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotBlank();

        // Verify password in DB has changed
        AccountEntity updatedAccount = accountRepository.findById(testAccount.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("newPassword456", updatedAccount.getPasswordHash())).isTrue();
        
        // Verify OTP is deleted/used
        assertThat(passwordResetTokenRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("changePassword - Should change password in DB")
    void changePassword_ShouldChangeInDB() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword123", "newPassword456");

        // Act
        passwordService.changePassword(testAccount.getId(), request);

        // Assert
        AccountEntity updatedAccount = accountRepository.findById(testAccount.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("newPassword456", updatedAccount.getPasswordHash())).isTrue();
    }
}
