package com.example.the_cheaper.interfaces.rest.controller.admin;

import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.ApiResponse;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.UserResponse;
import com.example.the_cheaper.application.usecase.account.ManageAccountUseCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
public class AdminUserController {

    private final ManageAccountUseCase manageAccountUseCase;

    public AdminUserController(ManageAccountUseCase manageAccountUseCase) {
        this.manageAccountUseCase = manageAccountUseCase;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserResponse>>> listAccounts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserResponse> response = manageAccountUseCase.listAccounts();
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách tài khoản thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/accounts"));
        }
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<UserResponse>> createAdminAccount(@RequestBody Object request) {
        try {
            throw new NotImplementedException("Chức năng tạo tài khoản admin chưa được triển khai");
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/accounts"));
        }
    }

    @GetMapping("/{account_id}")
    public ResponseEntity<ApiResponse<UserResponse>> getAccountDetail(
            @PathVariable("account_id") Long accountId) {
        try {
            throw new NotImplementedException("Chức năng xem chi tiết tài khoản chưa được triển khai");
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(),
                            "/api/admin/accounts/" + accountId));
        }
    }

    @PutMapping("/{account_id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateAccount(
            @PathVariable("account_id") Long accountId,
            @RequestBody Object request) {
        try {
            throw new NotImplementedException("Chức năng cập nhật tài khoản chưa được triển khai");
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(),
                            "/api/admin/accounts/" + accountId));
        }
    }

    @DeleteMapping("/{account_id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @PathVariable("account_id") Long accountId) {
        try {
            manageAccountUseCase.deleteAccount(accountId);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa tài khoản thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(),
                            "/api/admin/accounts/" + accountId));
        }
    }

}
