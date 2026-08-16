package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminOptionAttributeRequest;
import com.example.the_cheaper.dto.request.admin.AdminOptionValueRequest;
import com.example.the_cheaper.dto.response.admin.AdminMaterialResponse;
import com.example.the_cheaper.dto.response.admin.AdminOptionAttributeResponse;
import com.example.the_cheaper.dto.response.admin.AdminOptionValueResponse;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.exception.ResourceAlreadyExistsException;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.service.admin.AdminOptionAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.annotation.CurrentUser;

import java.util.List;


@RestController
@RequestMapping(Shared.BASE_URL_ADMIN)
@RequiredArgsConstructor
public class AdminOptionAttributeController {

    private final AdminOptionAttributeService optionAttributeService;

    @GetMapping("/option-attributes")
    public ResponseEntity<ApiResponse<List<AdminOptionAttributeResponse>>> listOptionAttributes(
            @CurrentUser AccountEntity currentUser) {
        try {
            List<AdminOptionAttributeResponse> response =
                    optionAttributeService.listOptionAttributes(currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách thuộc tính thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes"));
        }
    }

    @GetMapping("/option-attributes/search")
    public ResponseEntity<ApiResponse<Page<AdminOptionAttributeResponse>>> searchProducts(
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @CurrentUser AccountEntity currentUser
    ) {
        try {
            Page<AdminOptionAttributeResponse> response = optionAttributeService.searchOptionAttribute(name, currentUser, page, limit);
            return ResponseEntity.ok(ApiResponse.success(response, "Tìm tài khoản thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi server: " + e.getMessage(),
                            "/api/accounts/search"));
        }
    }

    @PostMapping("/option-attributes")
    public ResponseEntity<ApiResponse<AdminOptionAttributeResponse>> createOptionAttribute(
            @Valid @RequestBody AdminOptionAttributeRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminOptionAttributeResponse response =
                    optionAttributeService.createOptionAttribute(request, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Tạo thuộc tính thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes"));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage(),
                            "/api/admin/option-attributes"));
        }
    }


    @GetMapping("/option-attributes/{id}")
    public ResponseEntity<ApiResponse<AdminOptionAttributeResponse>> getOptionAttributeDetail(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminOptionAttributeResponse response = optionAttributeService.getOptionAttributeDetail(
                    id,
                    currentUser
            );

            return ResponseEntity.ok(
                    ApiResponse.success(
                        response,
                        "Lấy chi tiết thuộc tính thành công"
                    )
            );
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(
                            ApiResponse.error(HttpStatus.FORBIDDEN.value(),
                            e.getMessage(),
                            "/api/admin/option-attributes/" + id)
                    );
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            ApiResponse.error(
                                    HttpStatus.NOT_FOUND.value(),
                                    e.getMessage(),
                                    "/api/admin/option-attributes/" + id
                            )
                    );
        }
    }


    @PutMapping("/option-attributes/{id}")
    public ResponseEntity<ApiResponse<AdminOptionAttributeResponse>> updateOptionAttribute(
            @PathVariable Long id,
            @Valid @RequestBody AdminOptionAttributeRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminOptionAttributeResponse response =
                    optionAttributeService.updateOptionAttribute(id, request, currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật thuộc tính thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        }
    }


    @DeleteMapping("/option-attributes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOptionAttribute(
            @PathVariable Long id,
            @CurrentUser AccountEntity currentUser) {
        try {
            optionAttributeService.deleteOptionAttribute(id, currentUser);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa thuộc tính thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + id));
        }
    }


    @GetMapping("/option-attributes/{attributeId}/values")
    public ResponseEntity<ApiResponse<List<AdminOptionValueResponse>>> listValuesByAttribute(
            @PathVariable Long attributeId,
            @CurrentUser AccountEntity currentUser) {
        try {
            List<AdminOptionValueResponse> response =
                    optionAttributeService.listValuesByAttribute(attributeId, currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách giá trị thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values"));
        }
    }


    @PostMapping("/option-attributes/{attributeId}/values")
    public ResponseEntity<ApiResponse<AdminOptionValueResponse>> addValueToAttribute(
            @PathVariable Long attributeId,
            @Valid @RequestBody AdminOptionValueRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminOptionValueResponse response =
                    optionAttributeService.addValueToAttribute(attributeId, request, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Thêm giá trị thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values"));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values"));
        }
    }


    @DeleteMapping("/option-attributes/{attributeId}/values/{valueId}")
    public ResponseEntity<ApiResponse<Void>> deleteValue(
            @PathVariable Long attributeId,
            @PathVariable Long valueId,
            @CurrentUser AccountEntity currentUser) {
        try {
            optionAttributeService.deleteValue(attributeId, valueId, currentUser);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa giá trị thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values/" + valueId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage(),
                            "/api/admin/option-attributes/" + attributeId + "/values/" + valueId));
        }
    }
}
