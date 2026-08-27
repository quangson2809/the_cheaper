package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.UpdateRolePermissionsRequest;
import com.example.the_cheaper.dto.response.admin.AdminRolePermissionResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.service.admin.AdminRolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Shared.BASE_URL_ADMIN + "/roles")
@RequiredArgsConstructor
public class AdminRolePermissionController {

    private final AdminRolePermissionService adminRolePermissionService;

    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_READ')")
    public ResponseEntity<ApiResponse<List<AdminRolePermissionResponse>>> getPermissions(
            @PathVariable Long roleId,
            @CurrentUser AccountEntity currentUser) {
        List<AdminRolePermissionResponse> response = adminRolePermissionService.getPermissions(roleId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy permission của role thành công"));
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_UPDATE')")
    public ResponseEntity<ApiResponse<List<AdminRolePermissionResponse>>> replacePermissions(
            @PathVariable Long roleId,
            @Valid @RequestBody UpdateRolePermissionsRequest request,
            @CurrentUser AccountEntity currentUser) {
        List<AdminRolePermissionResponse> response = adminRolePermissionService.replacePermissions(roleId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật permission của role thành công"));
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_GRANT')")
    public ResponseEntity<ApiResponse<AdminRolePermissionResponse>> grantPermission(
            @PathVariable Long roleId,
            @PathVariable Long permissionId,
            @CurrentUser AccountEntity currentUser) {
        AdminRolePermissionResponse response = adminRolePermissionService.grantPermission(roleId, permissionId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Cấp permission cho role thành công"));
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_REVOKE')")
    public ResponseEntity<ApiResponse<Void>> revokePermission(
            @PathVariable Long roleId,
            @PathVariable Long permissionId,
            @CurrentUser AccountEntity currentUser) {
        adminRolePermissionService.revokePermission(roleId, permissionId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Hủy permission của role thành công"));
    }
}
