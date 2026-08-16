package com.example.the_cheaper.dto.response.admin;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private BigDecimal totalRevenue;
    private int totalOrders;
    private int totalUsers;
}


