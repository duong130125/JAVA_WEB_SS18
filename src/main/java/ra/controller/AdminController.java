package ra.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ra.service.AdminService;
import ra.dto.DashboardStats;
import ra.entity.User;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Check admin access
    private boolean isAdmin(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    @GetMapping("")
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        DashboardStats stats = adminService.getDashboardStatistics();
        model.addAttribute("stats", stats);
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("currentTime", LocalDateTime.now());

        return "admin/layout";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        DashboardStats stats = adminService.getDashboardStatistics();
        model.addAttribute("stats", stats);
        model.addAttribute("currentPage", "dashboard");

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        model.addAttribute("users", adminService.getAllUsers());
        model.addAttribute("currentPage", "users");

        return "admin/users";
    }

    @GetMapping("/products")
    public String manageProducts(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        model.addAttribute("products", adminService.getAllProducts());
        model.addAttribute("currentPage", "products");

        return "admin/products";
    }

    @GetMapping("/orders")
    public String manageOrders(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        model.addAttribute("orders", adminService.getAllOrders());
        model.addAttribute("currentPage", "orders");

        return "admin/orders";
    }

    @GetMapping("/revenue")
    @ResponseBody
    public Double getRevenue(@RequestParam int year,
                             @RequestParam(required = false) Integer month,
                             HttpSession session) {
        if (!isAdmin(session)) {
            return 0.0;
        }

        if (month != null) {
            return adminService.getMonthlyRevenue(year, month);
        } else {
            return adminService.getYearlyRevenue(year);
        }
    }
}