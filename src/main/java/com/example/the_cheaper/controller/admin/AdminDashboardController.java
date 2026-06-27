package com.example.the_cheaper.controller.admin;

import com.example.the_cheaper.config.Shared;
import com.example.the_cheaper.dto.ApiResponse;
import com.example.the_cheaper.dto.request.common.SearchRequest;
import com.example.the_cheaper.dto.response.admin.AdminDashboardResponse;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.service.admin.AdminDashboardService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.Year;
import com.example.the_cheaper.dto.response.admin.MonthlyQuantityResponse;
import com.example.the_cheaper.dto.response.admin.MonthlyRevenueResponse;
import com.example.the_cheaper.dto.response.admin.OrderStatusRatioResponse;
import org.springframework.web.bind.annotation.RestController;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.security.CurrentUser;

@RestController
@RequestMapping(Shared.BASE_URL_ADMIN)
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getStats(
            @CurrentUser AccountEntity currentUser) {
        try {
            AdminDashboardResponse response = adminDashboardService.getDashboardStats(currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy thống kê dashboard thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/stats"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Object>>> globalSearch(
            @ModelAttribute SearchRequest request,
            @CurrentUser AccountEntity currentUser) {
        try {
            List<Object> response = adminDashboardService.globalSearch(request, currentUser);
            return ResponseEntity.ok(ApiResponse.success(response, "Tìm kiếm thành công"));
        } catch (NotImplementedException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error(HttpStatus.NOT_IMPLEMENTED.value(), e.getMessage(), "/api/admin/search"));
        }
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<ApiResponse<List<MonthlyRevenueResponse>>> getMonthlyRevenue(
            @RequestParam(required = false) Integer year,
            @CurrentUser AccountEntity currentUser) {
        if (year == null) {
            year = Year.now().getValue();
        }
        List<MonthlyRevenueResponse> data = adminDashboardService.getMonthlyRevenue(year, currentUser);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy thống kê doanh thu thành công"));
    }

    @GetMapping("/monthly-quantity")
    public ResponseEntity<ApiResponse<List<MonthlyQuantityResponse>>> getMonthlySoldQuantity(
            @RequestParam(required = false) Integer year,
            @CurrentUser AccountEntity currentUser) {
        if (year == null) {
            year = Year.now().getValue();
        }
        List<MonthlyQuantityResponse> data = adminDashboardService.getMonthlySoldQuantity(year, currentUser);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy thống kê sản lượng bán ra thành công"));
    }

    @GetMapping("/order-status")
    public ResponseEntity<ApiResponse<List<OrderStatusRatioResponse>>> getOrderStatusRatios(
            @CurrentUser AccountEntity currentUser) {
        List<OrderStatusRatioResponse> data = adminDashboardService.getOrderStatusRatios(currentUser);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy thống kê trạng thái đơn hàng thành công"));
    }
}


