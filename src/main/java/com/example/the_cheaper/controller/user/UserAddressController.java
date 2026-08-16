package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.user.UserAddressCreateRequest;
import com.example.the_cheaper.dto.request.user.UserAddressUpdateRequest;
import com.example.the_cheaper.dto.response.user.UserAddressResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.service.user.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserAddressResponse>>> getUserAddresses(
            @CurrentUser AccountEntity currentUser) {
        try {
            List<UserAddressResponse> response = addressService.getUserAddresses(currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách địa chỉ thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/addresses"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserAddressResponse>> getAddressById(
            @CurrentUser AccountEntity currentUser,
            @PathVariable Long id) {
        try {
            UserAddressResponse response = addressService.getAddressById(currentUser.getId(), id);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết địa chỉ thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/addresses/" + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(),
                            e.getMessage(), "/api/addresses/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/addresses/" + id));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserAddressResponse>> createAddress(
            @CurrentUser AccountEntity currentUser,
            @Valid @RequestBody UserAddressCreateRequest request) {
        try {
            UserAddressResponse response = addressService.createAddress(currentUser.getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Thêm địa chỉ mới thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/addresses"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/addresses"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserAddressResponse>> updateAddress(
            @CurrentUser AccountEntity currentUser,
            @PathVariable Long id,
            @Valid @RequestBody UserAddressUpdateRequest request) {
        try {
            UserAddressResponse response = addressService.updateAddress(currentUser.getId(), id, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật địa chỉ thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/addresses/" + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(), "/api/addresses/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/addresses/" + id));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @CurrentUser AccountEntity currentUser,
            @PathVariable Long id) {
        try {
            addressService.deleteAddress(currentUser.getId(), id);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa địa chỉ thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/addresses/" + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(), "/api/addresses/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/addresses/" + id));
        }
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            @CurrentUser AccountEntity currentUser,
            @PathVariable Long id) {
        try {
            addressService.setDefaultAddress(currentUser.getId(), id);
            return ResponseEntity.ok(ApiResponse.success(null, "Thiết lập địa chỉ mặc định thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), "/api/addresses/" + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(),
                            e.getMessage(), "/api/addresses/" + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Lỗi server: " + e.getMessage(), "/api/addresses/" + id));
        }
    }
}
