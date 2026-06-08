package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminMaterialRequest;
import com.example.the_cheaper.dto.response.admin.AdminMaterialResponse;
import com.example.the_cheaper.service.admin.AdminMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.security.CurrentUser;
import java.util.List;

@RestController
@RequestMapping(Shared.BASE_URL_ADMIN)
@RequiredArgsConstructor
public class AdminMaterialController {

    private final AdminMaterialService adminMaterialService;

    @GetMapping("/materials")
    public ResponseEntity<ApiResponse<List<AdminMaterialResponse>>> listMaterials(
            @CurrentUser AccountEntity currentUser) {
        List<AdminMaterialResponse> response = adminMaterialService.listMaterials(currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách chất liệu thành công"));
    }

    @PostMapping("/materials")
    public ResponseEntity<ApiResponse<AdminMaterialResponse>> createMaterial(
            @Valid @RequestBody AdminMaterialRequest request,
            @CurrentUser AccountEntity currentUser) {
        AdminMaterialResponse response = adminMaterialService.createMaterial(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tạo chất liệu thành công"));
    }

    @GetMapping("/materials/{id}")
    public ResponseEntity<ApiResponse<AdminMaterialResponse>> getMaterialDetail(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        AdminMaterialResponse response = adminMaterialService.getMaterialDetail(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết chất liệu thành công"));
    }

    @PutMapping("/materials/{id}")
    public ResponseEntity<ApiResponse<AdminMaterialResponse>> updateMaterial(
            @PathVariable Long id,
            @Valid @RequestBody AdminMaterialRequest request,
            @CurrentUser AccountEntity currentUser) {
        AdminMaterialResponse response = adminMaterialService.updateMaterial(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật chất liệu thành công"));
    }

    @PatchMapping("/materials/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateMaterialStatus(
            @PathVariable Long id,
            @RequestParam int status,
            @CurrentUser AccountEntity currentUser) {
        adminMaterialService.updateMaterialStatus(id, status, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật chất liệu thành công"));
    }

    @DeleteMapping("/materials/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        adminMaterialService.deleteMaterial(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa chất liệu thành công"));
    }
}


