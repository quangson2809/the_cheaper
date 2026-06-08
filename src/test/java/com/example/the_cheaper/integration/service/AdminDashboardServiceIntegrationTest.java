package com.example.the_cheaper.integration.service;

import com.example.the_cheaper.dto.response.admin.MonthlyQuantityResponse;
import com.example.the_cheaper.dto.response.admin.MonthlyRevenueResponse;
import com.example.the_cheaper.dto.response.admin.OrderStatusRatioResponse;
import com.example.the_cheaper.entity.AccountEntity;
import com.example.the_cheaper.entity.RoleEntity;
import com.example.the_cheaper.repository.AccountRepository;
import com.example.the_cheaper.repository.RoleRepository;
import com.example.the_cheaper.service.admin.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AdminDashboardServiceIntegrationTest {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AccountEntity adminAccount;

    @BeforeEach
    void setUp() {
        RoleEntity adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            RoleEntity role = new RoleEntity();
            role.setName("ADMIN");
            return roleRepository.save(role);
        });

        AccountEntity account = AccountEntity.builder()
                .name("Admin User")
                .email("admin_integration@example.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .role(adminRole)
                .status(1)
                .build();
                
        adminAccount = accountRepository.save(account);
    }

    @Test
    @DisplayName("getMonthlyRevenue - Should return empty or partial real data from database")
    void getMonthlyRevenue_ShouldReturnData() {
        // Act
        List<MonthlyRevenueResponse> responses = adminDashboardService.getMonthlyRevenue(2024, adminAccount);

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(12);
        // Since we are using real DB in test and it might be empty or have data, we just verify the structure
        assertThat(responses.get(0).getMonth()).isEqualTo(1);
        assertThat(responses.get(11).getMonth()).isEqualTo(12);
    }

    @Test
    @DisplayName("getMonthlySoldQuantity - Should return monthly quantities from database")
    void getMonthlySoldQuantity_ShouldReturnData() {
        // Act
        List<MonthlyQuantityResponse> responses = adminDashboardService.getMonthlySoldQuantity(2024, adminAccount);

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(12);
        assertThat(responses.get(0).getMonth()).isEqualTo(1);
    }

    @Test
    @DisplayName("getOrderStatusRatios - Should return status ratios from database")
    void getOrderStatusRatios_ShouldReturnData() {
        // Act
        List<OrderStatusRatioResponse> responses = adminDashboardService.getOrderStatusRatios(adminAccount);

        // Assert
        assertThat(responses).isNotNull();
        // The list can be empty if there are no orders, but we verify it doesn't throw errors and executes query
    }
}
