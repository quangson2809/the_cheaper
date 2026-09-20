package com.example.the_cheaper.account.controller.user;

import com.example.the_cheaper.common.dto.ApiResponse;
import com.example.the_cheaper.auth.dto.request.ChangePasswordRequest;
import com.example.the_cheaper.account.dto.request.user.UserUpdateProfileRequest;
import com.example.the_cheaper.account.dto.response.user.UserAccountResponse;
import com.example.the_cheaper.account.entity.AccountEntity;
import com.example.the_cheaper.common.exception.InvalidInputException;
import com.example.the_cheaper.common.exception.ResourceNotFoundException;
import com.example.the_cheaper.security.CurrentUser;
import com.example.the_cheaper.auth.service.PasswordService;
import com.example.the_cheaper.account.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final PasswordService passwordService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserAccountResponse>> getAccountDetail(
            @CurrentUser AccountEntity currentUser) {
        try {
            UserAccountResponse response = userAccountService.getAccountDetail(currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy thông tin tài khoản thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/user/account"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(), "/api/user/account"));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserAccountResponse>> updateProfile(
            @CurrentUser AccountEntity currentUser,
            @Valid @RequestBody UserUpdateProfileRequest request) {
        try {
            UserAccountResponse response = userAccountService.updateProfile(currentUser.getId(), request);
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật thông tin tài khoản thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/user/account"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(), "/api/user/account"));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @CurrentUser AccountEntity currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            passwordService.changePassword(currentUser.getId(), request);
            return ResponseEntity.ok(ApiResponse.success(null, "Đổi mật khẩu thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), "/api/user/account/password"));
        } catch (InvalidInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/user/account/password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(), "/api/user/account/password"));
        }
    }
}
