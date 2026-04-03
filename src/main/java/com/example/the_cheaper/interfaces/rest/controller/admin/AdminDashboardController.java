package com.example.the_cheaper.interfaces.rest.controller.admin;

import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.ApiResponse;
import com.example.the_cheaper.interfaces.rest.dto.request.common.SearchRequest;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.DashboardResponse;
import com.example.the_cheaper.application.usecase.account.GetDashboardUseCase;
import com.example.the_cheaper.interfaces.rest.mapper.user.AccountMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final GetDashboardUseCase getDashboardUseCase;
    private final AccountMapper accountMapper;

    public AdminDashboardController(GetDashboardUseCase getDashboardUseCase, AccountMapper accountMapper) {
        this.getDashboardUseCase = getDashboardUseCase;
        this.accountMapper = accountMapper;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardResponse>> getStats() {
        try {
            DashboardResponse response = getDashboardUseCase.getDashboardStats();
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy thống kê dashboard thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/stats"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Object>>> globalSearch(@ModelAttribute SearchRequest request) {
        try {
            List<Object> response = getDashboardUseCase.globalSearch(accountMapper.toQuery(request));
            return ResponseEntity.ok(ApiResponse.success(response, "Tìm kiếm thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/search"));
        }
    }

}
