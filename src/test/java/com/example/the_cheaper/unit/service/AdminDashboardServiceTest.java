package com.example.the_cheaper.unit.service;

import com.example.the_cheaper.dto.response.admin.MonthlyRevenueResponse;
import com.example.the_cheaper.dto.response.admin.MonthlyQuantityResponse;
import com.example.the_cheaper.dto.response.admin.OrderStatusRatioResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.OrderStatus;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.fixtures.AccountFixtures;
import com.example.the_cheaper.repository.OrderRepository;
import com.example.the_cheaper.service.admin.AdminDashboardService;
import com.example.the_cheaper.service.admin.AdminProtectedAccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private AdminProtectedAccess adminProtectedAccess;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    @Test
    @DisplayName("getDashboardStats - Should throw NotImplementedException")
    void getDashboardStats_ShouldThrowNotImplementedException() {
        // Arrange
        AccountEntity admin = AccountFixtures.createActiveAdminAccount();
        doNothing().when(adminProtectedAccess).adminAccess(admin);

        // Act & Assert
        assertThatThrownBy(() -> adminDashboardService.getDashboardStats(admin))
                .isInstanceOf(NotImplementedException.class)
                .hasMessage("Chức năng dashboard chưa được triển khai");
                
        verify(adminProtectedAccess).adminAccess(admin);
    }

    @Test
    @DisplayName("getMonthlyRevenue - Should return full 12 months data")
    void getMonthlyRevenue_ShouldReturn12MonthsData() {
        // Arrange
        int year = 2024;
        AccountEntity admin = AccountFixtures.createActiveAdminAccount();
        
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, new BigDecimal("1000.50")});
        mockResults.add(new Object[]{5, new BigDecimal("5000.00")});
        
        when(orderRepository.getMonthlyRevenue(year)).thenReturn(mockResults);
        doNothing().when(adminProtectedAccess).adminAccess(admin);

        // Act
        List<MonthlyRevenueResponse> responses = adminDashboardService.getMonthlyRevenue(year, admin);

        // Assert
        assertThat(responses).hasSize(12);
        assertThat(responses.get(0).getMonth()).isEqualTo(1);
        assertThat(responses.get(0).getRevenue()).isEqualByComparingTo("1000.50");
        
        assertThat(responses.get(1).getMonth()).isEqualTo(2);
        assertThat(responses.get(1).getRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        
        assertThat(responses.get(4).getMonth()).isEqualTo(5);
        assertThat(responses.get(4).getRevenue()).isEqualByComparingTo("5000.00");
        
        verify(adminProtectedAccess).adminAccess(admin);
        verify(orderRepository).getMonthlyRevenue(year);
    }

    @Test
    @DisplayName("getMonthlySoldQuantity - Should return full 12 months data")
    void getMonthlySoldQuantity_ShouldReturn12MonthsData() {
        // Arrange
        int year = 2024;
        AccountEntity admin = AccountFixtures.createActiveAdminAccount();
        
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{2, 150L});
        mockResults.add(new Object[]{12, 500L});
        
        when(orderRepository.getMonthlySoldQuantity(year)).thenReturn(mockResults);
        doNothing().when(adminProtectedAccess).adminAccess(admin);

        // Act
        List<MonthlyQuantityResponse> responses = adminDashboardService.getMonthlySoldQuantity(year, admin);

        // Assert
        assertThat(responses).hasSize(12);
        assertThat(responses.get(1).getMonth()).isEqualTo(2);
        assertThat(responses.get(1).getQuantity()).isEqualTo(150L);
        
        assertThat(responses.get(11).getMonth()).isEqualTo(12);
        assertThat(responses.get(11).getQuantity()).isEqualTo(500L);
        
        assertThat(responses.get(5).getQuantity()).isEqualTo(0L);
        
        verify(adminProtectedAccess).adminAccess(admin);
        verify(orderRepository).getMonthlySoldQuantity(year);
    }

    @Test
    @DisplayName("getOrderStatusRatios - Should return correct percentages")
    void getOrderStatusRatios_ShouldReturnCorrectPercentages() {
        // Arrange
        AccountEntity admin = AccountFixtures.createActiveAdminAccount();
        
        List<Object[]> mockResults = new ArrayList<>();
        // Total = 200 orders
        mockResults.add(new Object[]{OrderStatus.DELIVERED, 150L}); // 75%
        mockResults.add(new Object[]{OrderStatus.PENDING, 50L});    // 25%
        
        when(orderRepository.getOrderStatusCounts()).thenReturn(mockResults);
        doNothing().when(adminProtectedAccess).adminAccess(admin);

        // Act
        List<OrderStatusRatioResponse> responses = adminDashboardService.getOrderStatusRatios(admin);

        // Assert
        assertThat(responses).hasSize(2);
        
        OrderStatusRatioResponse completedRatio = responses.stream()
                .filter(r -> r.getStatus().equals("COMPLETED"))
                .findFirst().orElseThrow();
        assertThat(completedRatio.getCount()).isEqualTo(150L);
        assertThat(completedRatio.getPercentage()).isEqualTo(75.0);
        
        OrderStatusRatioResponse pendingRatio = responses.stream()
                .filter(r -> r.getStatus().equals("PENDING"))
                .findFirst().orElseThrow();
        assertThat(pendingRatio.getCount()).isEqualTo(50L);
        assertThat(pendingRatio.getPercentage()).isEqualTo(25.0);
        
        verify(adminProtectedAccess).adminAccess(admin);
        verify(orderRepository).getOrderStatusCounts();
    }
}
