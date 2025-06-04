package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.service.AdminService;
import ra.service.UserService;
import ra.service.ProductService;
import ra.dto.DashboardStats;
import ra.entity.User;
import ra.entity.Product;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // Check admin access
    private boolean isAdmin(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    // Phương thức helper để setup model chung
    private void setupCommonModel(Model model, String currentPage) {
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("currentTime", LocalDateTime.now());
    }

    @GetMapping("")
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        DashboardStats stats = adminService.getDashboardStatistics();
        model.addAttribute("stats", stats);
        setupCommonModel(model, "dashboard");

        return "admin/dashboard"; // Trả về template riêng cho dashboard
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        DashboardStats stats = adminService.getDashboardStatistics();
        model.addAttribute("stats", stats);
        setupCommonModel(model, "dashboard");

        return "admin/dashboard"; // Trả về template riêng cho dashboard
    }

    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        model.addAttribute("users", adminService.getAllUsers());
        setupCommonModel(model, "users");

        return "admin/users"; // Trả về template riêng cho users
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String role,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        try {
            if (userService.existsByUsername(username)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
                return "redirect:/admin/users";
            }

            if (userService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã được sử dụng!");
                return "redirect:/admin/users";
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole(role);
            newUser.setIsActive(true);

            userService.saveUser(newUser);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm user thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa user thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/products")
    public String manageProducts(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        model.addAttribute("products", adminService.getAllProducts());
        setupCommonModel(model, "products");

        return "admin/products"; // Trả về template riêng cho products
    }

    @PostMapping("/products/add")
    public String addProduct(@RequestParam String name,
                             @RequestParam String description,
                             @RequestParam Double price,
                             @RequestParam Integer stockQuantity,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        try {
            Product newProduct = new Product();
            newProduct.setName(name);
            newProduct.setDescription(description);
            newProduct.setPrice(price);
            newProduct.setStockQuantity(stockQuantity);
            newProduct.setIsActive(true);

            productService.saveProduct(newProduct);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/toggle/{id}")
    public String toggleProductStatus(@PathVariable Long id,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        try {
            productService.toggleProductStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=access_denied";
        }

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

//    @GetMapping("/orders")
//    public String manageOrders(HttpSession session, Model model) {
//        if (!isAdmin(session)) {
//            return "redirect:/login?error=access_denied";
//        }
//
//        model.addAttribute("orders", adminService.getAllOrders());
//        setupCommonModel(model, "orders");
//
//        return "admin/orders"; // Trả về template riêng cho orders
//    }
//
//    @PostMapping("/orders/updateStatus/{id}")
//    public String updateOrderStatus(@PathVariable Long id,
//                                    @RequestParam String status,
//                                    HttpSession session,
//                                    RedirectAttributes redirectAttributes) {
//        if (!isAdmin(session)) {
//            return "redirect:/login?error=access_denied";
//        }
//
//        try {
//            adminService.updateOrderStatus(id, status);
//            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
//        }
//
//        return "redirect:/admin/orders";
//    }

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

    // API endpoint để load nội dung động qua AJAX (nếu cần)
    @GetMapping("/api/load-content/{page}")
    @ResponseBody
    public String loadContent(@PathVariable String page, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "error";
        }

        switch (page) {
            case "dashboard":
                DashboardStats stats = adminService.getDashboardStatistics();
                model.addAttribute("stats", stats);
                break;
            case "users":
                model.addAttribute("users", adminService.getAllUsers());
                break;
            case "products":
                model.addAttribute("products", adminService.getAllProducts());
                break;
            case "orders":
                model.addAttribute("orders", adminService.getAllOrders());
                break;
            default:
                return "error";
        }

        setupCommonModel(model, page);
        return "success";
    }
}