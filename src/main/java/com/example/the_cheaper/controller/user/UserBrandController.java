package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.response.user.UserBrandResponse;
import com.example.the_cheaper.service.product.BrandService;
import lombok.RequiredArgsConstructor;
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


