package com.example.the_cheaper.integration.service;

import com.example.the_cheaper.dto.request.auth.LoginRequest;
import com.example.the_cheaper.dto.request.auth.RegisterRequest;
import com.example.the_cheaper.dto.response.auth.AuthResponse;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Prepare required roles in database for integration test
        if (roleRepository.findByName("USER").isEmpty()) {
            RoleEntity userRole = new RoleEntity();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }
    }

    @Test
    @DisplayName("register - Should save new account to database")
    void register_ShouldSaveToDatabase() {
        // Arrange
        String testEmail = "newuser@test.com";
        RegisterRequest request = new RegisterRequest("New User", testEmail, "securePassword123");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(testEmail);
        assertThat(response.getAccessToken()).isNotBlank();

        // Verify database
        assertThat(accountRepository.existsByEmail(testEmail)).isTrue();
    }

    @Test
    @DisplayName("register - Should throw exception if email already exists in DB")
    void register_ShouldThrowException_IfEmailExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("User 1", "duplicate@test.com", "pass123");
        authService.register(request); // Register first time

        // Act & Assert (Register second time with same email)
        RegisterRequest duplicateRequest = new RegisterRequest("User 2", "duplicate@test.com", "pass456");
        assertThatThrownBy(() -> authService.register(duplicateRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Email đã được sử dụng");
    }

    @Test
    @DisplayName("login - Should authenticate successfully with correct credentials")
    void login_ShouldAuthenticateSuccessfully() {
        // Arrange
        String testEmail = "loginuser@test.com";
        String password = "securePassword123";
        RegisterRequest registerRequest = new RegisterRequest("Login User", testEmail, password);
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(testEmail, password);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(testEmail);
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
    }
}
