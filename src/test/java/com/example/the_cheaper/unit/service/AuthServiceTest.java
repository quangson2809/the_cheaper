package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.dto.request.auth.LoginRequest;
import com.example.the_cheaper.dto.request.auth.RegisterRequest;
import com.example.the_cheaper.dto.response.auth.AuthResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.exception.InvalidInputException;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.fixtures.AccountFixtures;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.security.CustomUserDetails;
import com.example.the_cheaper.security.JwtProvider;
import com.example.the_cheaper.service.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("register - Should throw ResourceAlreadyExistsException when email exists")
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123");
        when(accountRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Email đã được sử dụng");

        verify(accountRepository).existsByEmail(request.getEmail());
        verify(roleRepository, never()).findByName(anyString());
    }

    @Test
    @DisplayName("register - Should return AuthResponse on success")
    void register_ShouldReturnAuthResponse_OnSuccess() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123");
        RoleEntity userRole = AccountFixtures.createUserRole();
        
        when(accountRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_pwd");
        
        AccountEntity savedAccount = AccountFixtures.createActiveUserAccount();
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(savedAccount);
        
        when(jwtProvider.generateAccessToken(savedAccount)).thenReturn("access_token");
        when(jwtProvider.generateRefreshToken(savedAccount)).thenReturn("refresh_token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("user@test.com");
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        
        verify(accountRepository, times(2)).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("login - Should throw InvalidInputException on bad credentials")
    void login_ShouldThrowException_OnBadCredentials() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "wrong_pwd");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Thông tin đăng nhập không chính xác hoặc tài khoản bị vô hiệu hóa");
    }

    @Test
    @DisplayName("login - Should return AuthResponse on success")
    void login_ShouldReturnAuthResponse_OnSuccess() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        AccountEntity account = AccountFixtures.createActiveUserAccount();
        CustomUserDetails userDetails = new CustomUserDetails(account);
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
                
        when(jwtProvider.generateAccessToken(account)).thenReturn("access_token");
        when(jwtProvider.generateRefreshToken(account)).thenReturn("refresh_token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(account.getId());
        assertThat(response.getEmail()).isEqualTo(account.getEmail());
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
