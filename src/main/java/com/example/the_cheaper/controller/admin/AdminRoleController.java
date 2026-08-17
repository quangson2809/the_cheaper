package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminRoleCreateRequest;
import com.example.the_cheaper.dto.request.admin.AdminRoleUpdateRequest;
import com.example.the_cheaper.dto.response.admin.AdminRoleResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.service.admin.AdminRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Shared.BASE_URL_ADMIN + "/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponse<List<AdminRoleResponse>>> listRoles(
            @CurrentUser AccountEntity currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                adminRoleService.listRoles(currentUser),
                "Lấy danh sách role thành công"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponse<AdminRoleResponse>> getRole(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                adminRoleService.getRole(id, currentUser),
                "Lấy role thành công"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<ApiResponse<AdminRoleResponse>> createRole(
            @Valid @RequestBody AdminRoleCreateRequest request,
            @CurrentUser AccountEntity currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                adminRoleService.createRole(request, currentUser),
                "Tạo role thành công"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ApiResponse<AdminRoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody AdminRoleUpdateRequest request,
            @CurrentUser AccountEntity currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                adminRoleService.updateRole(id, request, currentUser),
                "Cập nhật role thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        adminRoleService.deleteRole(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa role thành công"));
    }
}
