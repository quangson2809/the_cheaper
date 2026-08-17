package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminCreateAdminRequest;
import com.example.the_cheaper.dto.request.admin.AdminUserFilterRequest;
import com.example.the_cheaper.dto.request.admin.AssignAccountRoleRequest;
import com.example.the_cheaper.dto.response.admin.AdminAccountResponse;
import com.example.the_cheaper.dto.response.admin.AdminAccountRoleResponse;
import com.example.the_cheaper.service.admin.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Shared.BASE_URL_ADMIN)
public class AdminAccountController {

    private final AdminUserService adminUserService;

    @PostMapping("/accounts")
    @PreAuthorize("hasAuthority('ACCOUNT_CREATE')")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> createAdminAccount(
            @Valid @RequestBody AdminCreateAdminRequest request) {
        AdminAccountResponse response = adminUserService.createAdminAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tạo tài khoản Admin thành công"));
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasAuthority('ACCOUNT_READ')")
    public ResponseEntity<ApiResponse<Page<AdminAccountResponse>>> listAccounts(
            AdminUserFilterRequest request) {
        Page<AdminAccountResponse> response = adminUserService.listAccounts(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách tài khoản thành công"));
    }

    @GetMapping("/accounts/search")
    @PreAuthorize("hasAuthority('ACCOUNT_READ')")
    public ResponseEntity<ApiResponse<Page<AdminAccountResponse>>> searchAccounts(
            @RequestParam String phone,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        Page<AdminAccountResponse> response = adminUserService.searchAccountByPhone(phone, page, limit);
        return ResponseEntity.ok(ApiResponse.success(response, "Tìm tài khoản thành công"));
    }

    @GetMapping("/accounts/{accountId}/role")
    @PreAuthorize("hasAuthority('ACCOUNT_ROLE_READ')")
    public ResponseEntity<ApiResponse<AdminAccountRoleResponse>> getAccountRole(
            @PathVariable Long accountId) {
        AdminAccountRoleResponse response = adminUserService.getAccountRole(accountId);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy role của tài khoản thành công"));
    }

    @PutMapping("/accounts/{accountId}/role")
    @PreAuthorize("hasAuthority('ACCOUNT_ROLE_UPDATE')")
    public ResponseEntity<ApiResponse<AdminAccountRoleResponse>> assignAccountRole(
            @PathVariable Long accountId,
            @Valid @RequestBody AssignAccountRoleRequest request) {
        AdminAccountRoleResponse response = adminUserService.assignAccountRole(accountId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Gán role cho tài khoản thành công"));
    }

    @DeleteMapping("/accounts/{accountId}")
    @PreAuthorize("hasAuthority('ACCOUNT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long accountId) {
        adminUserService.deleteAccount(accountId);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa tài khoản thành công"));
    }

    @PutMapping("/accounts/{accountId}/status")
    @PreAuthorize("hasAuthority('ACCOUNT_STATUS_UPDATE')")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> updateAccountStatus(
            @PathVariable Long accountId,
            @RequestParam int status) {
        AdminAccountResponse response = adminUserService.updateAccountStatus(accountId, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật trạng thái tài khoản thành công"));
    }
}
