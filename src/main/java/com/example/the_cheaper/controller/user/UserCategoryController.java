package com.example.the_cheaper.controller.user;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.admin.AdminCategoryRequest;
import com.example.the_cheaper.dto.response.admin.AdminCategoryResponse;
import com.example.the_cheaper.dto.response.user.UserCategoryResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.security.CurrentUser;
import com.example.the_cheaper.service.admin.AdminCategoryService;
import com.example.the_cheaper.service.product.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class UserCategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserCategoryResponse>>> listCategories() {
        System.out.println("URI = api/categoies");
        List<UserCategoryResponse> response = categoryService.listCategories();
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách danh mục thành công"));
    }
}


