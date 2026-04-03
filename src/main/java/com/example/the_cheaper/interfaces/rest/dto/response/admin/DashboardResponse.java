package com.example.the_cheaper.interfaces.rest.dto.response.admin;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalUsers;
    private List<ProductSales> topProducts;
}
