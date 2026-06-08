package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminBrandRequest;
import com.example.the_cheaper.dto.response.admin.AdminBrandResponse;
import com.example.the_cheaper.dto.response.user.UserBrandResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.security.CurrentUser;
import com.example.the_cheaper.service.admin.AdminBrandService;
import com.example.the_cheaper.service.product.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class UserBrandController {

    private final BrandService brandService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserBrandResponse>>> listBrands() {
        System.out.println("URI = api/brands");
        List<UserBrandResponse> response = brandService.listBrands();
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách thương hiệu thành công"));
    }
}


