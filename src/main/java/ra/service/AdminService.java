package ra.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.repository.AdminRepository;
import ra.dto.DashboardStats;
import ra.entity.User;
import ra.entity.Product;
import ra.entity.Order;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public DashboardStats getDashboardStatistics() {
        return adminRepository.getDashboardStats();
    }

    public List<User> getAllUsers() {
        return adminRepository.getAllUsers();
    }

    public List<Product> getAllProducts() {
        return adminRepository.getAllProducts();
    }

    public List<Order> getAllOrders() {
        return adminRepository.getAllOrders();
    }

    public Double getMonthlyRevenue(int year, int month) {
        return adminRepository.getMonthlyRevenue(year, month);
    }

    public Double getYearlyRevenue(int year) {
        return adminRepository.getYearlyRevenue(year);
    }
}