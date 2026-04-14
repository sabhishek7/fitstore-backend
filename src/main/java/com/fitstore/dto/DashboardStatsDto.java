package com.fitstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDto {

    private long totalOrders;
    private long todayOrders;
    private BigDecimal totalRevenue;
    private long activeProducts;
    private long pendingOrders;
}
