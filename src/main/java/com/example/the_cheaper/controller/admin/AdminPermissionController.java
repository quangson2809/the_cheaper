package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminPermissionCreateRequest;
import com.example.the_cheaper.dto.request.admin.AdminPermissionUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminPermissionResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.service.admin.AdminPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Shared.BASE_URL_ADMIN + "/permissions")
@RequiredArgsConstructor
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<Page<AdminPermissionResponse>>> listPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser AccountEntity currentUser) {
        Page<AdminPermissionResponse> response = adminPermissionService.listPermissions(page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách permission thành công"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<AdminPermissionResponse>> getPermission(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        AdminPermissionResponse response = adminPermissionService.getPermission(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy permission thành công"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public ResponseEntity<ApiResponse<AdminPermissionResponse>> createPermission(
            @Valid @RequestBody AdminPermissionCreateRequest request,
            @CurrentUser AccountEntity currentUser) {
        AdminPermissionResponse response = adminPermissionService.createPermission(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Tạo permission thành công"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
    public ResponseEntity<ApiResponse<AdminPermissionResponse>> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody AdminPermissionUpdateRequest request,
            @CurrentUser AccountEntity currentUser) {
        AdminPermissionResponse response = adminPermissionService.updatePermission(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật permission thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deletePermission(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        adminPermissionService.deletePermission(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa permission thành công"));
    }
}
