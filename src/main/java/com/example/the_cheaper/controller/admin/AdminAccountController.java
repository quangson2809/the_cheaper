package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminUserFilterRequest;
import com.example.the_cheaper.dto.response.admin.AdminAccountResponse;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.service.admin.AdminUserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.the_cheaper.dto.request.admin.AdminCreateAdminRequest;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.security.CurrentUser;
import jakarta.validation.Valid;

@RestController
@RequestMapping(Shared.BASE_URL_ADMIN)
public class AdminAccountController {

    private final AdminUserService adminUserService;

    public AdminAccountController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping("/accounts/admin")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> createAdminAccount(
            @Valid @RequestBody AdminCreateAdminRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminAccountResponse response = adminUserService.createAdminAccount(currentUser, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Tạo tài khoản Admin thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/admin/accounts/admin"));
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AdminAccountResponse>>> listAccounts(
            AdminUserFilterRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            List<AdminAccountResponse> response = adminUserService.listAccounts(currentUser, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách tài khoản thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/admin/accounts"));
        }
    }

    @DeleteMapping("/accounts/{account_id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @PathVariable("account_id") Long accountId) {
        try {
            adminUserService.deleteAccount(accountId);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa tài khoản thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                            "/api/admin/accounts/" + accountId));
        }
    }

    @PutMapping("/accounts/{account_id}/status")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> updateAccountStatus(
            @PathVariable("account_id") Long accountId,
            @RequestParam Integer status) {
        try {
            AdminAccountResponse response = adminUserService.updateAccountStatus(accountId, status);
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật trạng thái tài khoản thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                            "/api/admin/accounts/" + accountId + "/status"));
        }
    }
}
