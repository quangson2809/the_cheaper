package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminCreateAdminRequest;
import com.example.the_cheaper.dto.request.admin.AdminUserFilterRequest;
import com.example.the_cheaper.dto.request.admin.AssignAccountRoleRequest;
import com.example.the_cheaper.dto.response.admin.AdminAccountResponse;
import com.example.the_cheaper.dto.response.admin.AdminAccountRoleResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.service.admin.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Shared.BASE_URL_ADMIN)
public class AdminAccountController {

    private final AdminUserService adminUserService;

    @PostMapping("/accounts")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> createAdminAccount(
            @Valid @RequestBody AdminCreateAdminRequest request,
            @CurrentUser AccountEntity currentUser) {
        AdminAccountResponse response = adminUserService.createAdminAccount(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tạo tài khoản Admin thành công"));
    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<Page<AdminAccountResponse>>> listAccounts(
            AdminUserFilterRequest request,
            @CurrentUser AccountEntity currentUser) {
        Page<AdminAccountResponse> response = adminUserService.listAccounts(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách tài khoản thành công"));
    }

    @GetMapping("/accounts/search")
    public ResponseEntity<ApiResponse<Page<AdminAccountResponse>>> searchAccounts(
            @RequestParam String phone,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @CurrentUser AccountEntity currentUser) {
        Page<AdminAccountResponse> response =
                adminUserService.searchAccountByPhone(phone, currentUser, page, limit);
        return ResponseEntity.ok(ApiResponse.success(response, "Tìm tài khoản thành công"));
    }

    @GetMapping("/accounts/{accountId}/role")
    public ResponseEntity<ApiResponse<AdminAccountRoleResponse>> getAccountRole(
            @PathVariable Long accountId,
            @CurrentUser AccountEntity currentUser) {
        AdminAccountRoleResponse response = adminUserService.getAccountRole(currentUser, accountId);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy role của tài khoản thành công"));
    }

    @PutMapping("/accounts/{accountId}/role")
    public ResponseEntity<ApiResponse<AdminAccountRoleResponse>> assignAccountRole(
            @PathVariable Long accountId,
            @Valid @RequestBody AssignAccountRoleRequest request,
            @CurrentUser AccountEntity currentUser) {
        AdminAccountRoleResponse response =
                adminUserService.assignAccountRole(currentUser, accountId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Gán role cho tài khoản thành công"));
    }

    @DeleteMapping("/accounts/{account_id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @PathVariable("account_id") Long accountId) {
        adminUserService.deleteAccount(accountId);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa tài khoản thành công"));
    }

    @PutMapping("/accounts/{account_id}/status")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> updateAccountStatus(
            @CurrentUser AccountEntity currentUser,
            @PathVariable("account_id") Long accountId,
            @RequestParam int status) {
        AdminAccountResponse response = adminUserService.updateAccountStatus(currentUser, accountId, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật trạng thái tài khoản thành công"));
    }
}
