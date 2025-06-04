package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ra.entity.User;
import ra.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            switch (error) {
                case "invalid":
                    model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
                    break;
                case "access_denied":
                    model.addAttribute("errorMessage", "Bạn không có quyền truy cập trang này!");
                    break;
                default:
                    model.addAttribute("errorMessage", "Đã có lỗi xảy ra!");
            }
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        try {
            User user = userService.authenticate(username, password);
            if (user != null && user.getIsActive()) {
                session.setAttribute("currentUser", user);

                // Redirect based on role
                if ("ADMIN".equals(user.getRole())) {
                    return "redirect:/admin";
                } else {
                    return "redirect:/home";
                }
            } else {
                model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String email,
                                  @RequestParam String confirmPassword,
                                  Model model) {
        try {
            // Validate passwords match
            if (!password.equals(confirmPassword)) {
                model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
                return "register";
            }

            // Check if username already exists
            if (userService.existsByUsername(username)) {
                model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
                return "register";
            }

            // Check if email already exists
            if (userService.existsByEmail(email)) {
                model.addAttribute("errorMessage", "Email đã được sử dụng!");
                return "register";
            }

            // Create new user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password); // In production, should hash password
            newUser.setEmail(email);
            newUser.setRole("USER");
            newUser.setIsActive(true);

            userService.saveUser(newUser);
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "login";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", currentUser);
        return "home";
    }
}