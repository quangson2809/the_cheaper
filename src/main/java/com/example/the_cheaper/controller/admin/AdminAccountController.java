package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminUserFilterRequest;
import com.example.the_cheaper.dto.response.admin.AdminAccountResponse;
import com.example.the_cheaper.dto.response.admin.AdminProductOverviewResponse;
import com.example.the_cheaper.service.admin.AdminProtectedAccess;
import com.example.the_cheaper.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.example.the_cheaper.dto.request.admin.AdminCreateAdminRequest;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.annotation.CurrentUser;
import jakarta.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping(Shared.BASE_URL_ADMIN)
public class AdminAccountController {

    private final AdminUserService adminUserService;
    private final AdminProtectedAccess adminProtectedAccess;

    @PostMapping("/accounts")
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
    public ResponseEntity<ApiResponse<Page<AdminAccountResponse>>> listAccounts(
            AdminUserFilterRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            Page<AdminAccountResponse> response = adminUserService.listAccounts(currentUser, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách tài khoản thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "/api/admin/accounts"));
        }
    }

    @GetMapping("/accounts/search")
    public ResponseEntity<ApiResponse<Page<AdminAccountResponse>>> searchProducts(
            @RequestParam String phone,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @CurrentUser AccountEntity currentUser) {
        try {
            Page<AdminAccountResponse> response = adminUserService.searchAccountByPhone(phone, currentUser, page, limit);
            return ResponseEntity.ok(ApiResponse.success(response, "Tìm tài khoản thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(),
                            "/api/accounts/search"));
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
            @CurrentUser AccountEntity currentUser,
            @PathVariable("account_id") Long accountId,
            @RequestParam int status) {
        try {
            AdminAccountResponse response = adminUserService.updateAccountStatus(currentUser,accountId, status);
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật trạng thái tài khoản thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(),
                            "/api/admin/accounts/" + accountId + "/status"));
        }
    }
}
