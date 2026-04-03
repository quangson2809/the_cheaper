package com.example.the_cheaper.interfaces.rest.controller.auth;

import com.example.the_cheaper.domain.exception.InvalidInputException;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.domain.exception.ResourceNotFoundException;
import com.example.the_cheaper.interfaces.rest.dto.ApiResponse;
import com.example.the_cheaper.interfaces.rest.dto.request.auth.ChangePasswordRequest;
import com.example.the_cheaper.interfaces.rest.dto.request.auth.ForgotPasswordRequest;
import com.example.the_cheaper.interfaces.rest.dto.request.auth.LoginRequest;
import com.example.the_cheaper.interfaces.rest.dto.request.auth.RegisterRequest;
import com.example.the_cheaper.interfaces.rest.dto.response.auth.AuthResponse;
import com.example.the_cheaper.application.usecase.auth.*;
import com.example.the_cheaper.interfaces.rest.mapper.auth.AuthMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RegisterUseCase registerUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final AuthMapper authMapper;

    public AuthController(LoginUseCase loginUseCase, LogoutUseCase logoutUseCase, RegisterUseCase registerUseCase,
            ForgotPasswordUseCase forgotPasswordUseCase, ChangePasswordUseCase changePasswordUseCase,
            AuthMapper authMapper) {
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.registerUseCase = registerUseCase;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.authMapper = authMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = registerUseCase.register(authMapper.toCommand(request));
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Đăng ký thành công"));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/auth/register"));
        } catch (InvalidInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/auth/register"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/auth/register"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = loginUseCase.login(authMapper.toCommand(request));
            return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/auth/login"));
        } catch (InvalidInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/auth/login"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/auth/login"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            forgotPasswordUseCase.forgotPassword(authMapper.toCommand(request));
            return ResponseEntity.ok(ApiResponse.success(null, "Yêu cầu đặt lại mật khẩu đã được gửi"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/auth/forgot-password"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/auth/forgot-password"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam Long userId,
            @RequestBody ChangePasswordRequest request) {
        try {
            changePasswordUseCase.changePassword(authMapper.toCommand(userId, request));
            return ResponseEntity.ok(ApiResponse.success(null, "Đổi mật khẩu thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/auth/reset-password"));
        } catch (InvalidInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/auth/reset-password"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/auth/reset-password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam Long userId) {
        try {
            logoutUseCase.logout(userId);
            return ResponseEntity.ok(ApiResponse.success(null, "Đăng xuất thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/auth/logout"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh() {
        try {
            throw new NotImplementedException("Chức năng làm mới token chưa được triển khai");
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/auth/refresh"));
        }
    }

}
