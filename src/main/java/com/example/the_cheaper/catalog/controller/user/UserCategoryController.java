package com.example.the_cheaper.catalog.controller.user;

import com.example.the_cheaper.common.dto.ApiResponse;
import com.example.the_cheaper.catalog.dto.response.user.UserCategoryResponse;
import com.example.the_cheaper.catalog.service.CategoryService;
import lombok.RequiredArgsConstructor;
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


