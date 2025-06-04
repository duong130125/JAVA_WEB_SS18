package ra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private Long totalUsers;
    private Long totalProducts;
    private Long totalOrders;
    private Double totalRevenue;
    private Long activeUsers;
    private Long pendingOrders;
    private Double monthlyRevenue;
    private Double yearlyRevenue;
}
