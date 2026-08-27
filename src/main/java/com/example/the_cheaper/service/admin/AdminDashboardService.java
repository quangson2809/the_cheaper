package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.common.SearchRequest;
import com.example.the_cheaper.dto.response.admin.AdminDashboardResponse;
import com.example.the_cheaper.dto.response.admin.MonthlyQuantityResponse;
import com.example.the_cheaper.dto.response.admin.MonthlyRevenueResponse;
import com.example.the_cheaper.dto.response.admin.OrderStatusRatioResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.OrderStatus;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardStats(AccountEntity currentUser, int year) {
        LocalDateTime from = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime to = LocalDate.of(year + 1, 1, 1).atStartOfDay();

        int countUsers = accountRepository.countByCreatedBetween(from, to);
        int countOrders = orderRepository.countByPayAtBetween(from, to);
        BigDecimal revenue = orderRepository.revenueByPayAtBetween(from, to);

        return AdminDashboardResponse.builder()
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .totalOrders(countOrders)
                .totalUsers(countUsers)
                .build();
    }

    @Transactional(readOnly = true)
    public List<Object> globalSearch(SearchRequest request, AccountEntity currentUser) {
        throw new NotImplementedException("Chức năng tìm kiếm chưa được triển khai");
    }

    @Transactional(readOnly = true)
    public List<MonthlyRevenueResponse> getMonthlyRevenue(int year, AccountEntity currentUser) {
        List<Object[]> results = orderRepository.getMonthlyRevenue(year);

        Map<Integer, BigDecimal> revenueMap = results.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (BigDecimal) row[1]
                ));

        List<MonthlyRevenueResponse> responses = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            responses.add(new MonthlyRevenueResponse(i, revenueMap.getOrDefault(i, BigDecimal.ZERO)));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public List<MonthlyQuantityResponse> getMonthlySoldQuantity(int year, AccountEntity currentUser) {
        List<Object[]> results = orderRepository.getMonthlySoldQuantity(year);

        Map<Integer, Long> quantityMap = results.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> ((Number) row[1]).longValue()
                ));

        List<MonthlyQuantityResponse> responses = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            responses.add(new MonthlyQuantityResponse(i, quantityMap.getOrDefault(i, 0L)));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public List<OrderStatusRatioResponse> getOrderStatusRatios(AccountEntity currentUser) {
        List<Object[]> results = orderRepository.getOrderStatusCounts();

        long totalOrders = 0;
        for (Object[] row : results) {
            totalOrders += ((Number) row[1]).longValue();
        }

        List<OrderStatusRatioResponse> responses = new ArrayList<>();
        for (Object[] row : results) {
            OrderStatus status = (OrderStatus) row[0];
            long count = ((Number) row[1]).longValue();
            double percentage = totalOrders > 0 ? (double) count / totalOrders * 100 : 0;
            responses.add(new OrderStatusRatioResponse(status.name(), count,
                    Math.round(percentage * 100.0) / 100.0));
        }
        return responses;
    }
}
