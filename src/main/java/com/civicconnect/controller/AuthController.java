package com.civicconnect.controller;

import com.civicconnect.model.User;
import com.civicconnect.model.UserRole;
import com.civicconnect.model.IssueCategory;
import com.civicconnect.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = userService.loginUser(email, password);
            session.setAttribute("user", user);
            
            if (user.getRole() == UserRole.ADMIN) {
                return "redirect:/admin-dashboard";
            } else {
                return "redirect:/citizen-dashboard";
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // register.html
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String location,
                                 @RequestParam(defaultValue = "CITIZEN") UserRole role,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(name, email, password, role, location);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        session.invalidate();
        
        if (user != null && user.getRole() == UserRole.ADMIN) {
            return "redirect:/admin-logout";
        } else {
            return "redirect:/user-logout";
        }
    }

    @GetMapping("/user-logout")
    public String userLogoutPage() {
        return "user-logout";
    }

    @GetMapping("/admin-logout")
    public String adminLogoutPage() {
        return "admin-logout";
    }

    @GetMapping("/logout-page")
    public String logoutPage() {
        return "logout";
    }

    // User login endpoints
    @GetMapping("/user/login")
    public String userLoginPage() {
        return "user-login";
    }

    @PostMapping("/user/login")
    public String processUserLogin(@RequestParam String email,
                                  @RequestParam String password,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            User user = userService.loginUser(email, password);
            if (user.getRole() != UserRole.CITIZEN) {
                redirectAttributes.addFlashAttribute("error", "Invalid user credentials. Please use admin login for admin accounts.");
                redirectAttributes.addFlashAttribute("showSignIn", true);
                return "redirect:/user/login";
            }
            session.setAttribute("user", user);
            redirectAttributes.addFlashAttribute("success", "Welcome back, " + user.getName() + "!");
            return "redirect:/citizen-dashboard";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password. Please try again.");
            redirectAttributes.addFlashAttribute("showSignIn", true);
            return "redirect:/user/login";
        }
    }

    @PostMapping("/user/register")
    public String processUserRegister(@RequestParam String name,
                                     @RequestParam String email,
                                     @RequestParam String password,
                                     @RequestParam String location,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Validate input parameters
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("Name is required");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("Password is required");
            }
            if (location == null || location.trim().isEmpty()) {
                throw new RuntimeException("Location is required");
            }
            
            userService.registerUser(name.trim(), email.trim(), password, UserRole.CITIZEN, location.trim());
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please sign in.");
            redirectAttributes.addFlashAttribute("showSignIn", true);
            return "redirect:/user/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("showSignUp", true);
            return "redirect:/user/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed. Please try again.");
            redirectAttributes.addFlashAttribute("showSignUp", true);
            return "redirect:/user/login";
        }
    }

    // Admin login endpoints
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String processAdminLogin(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            User user = userService.loginUser(email, password);
            if (user.getRole() != UserRole.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "Invalid admin credentials. Please use user login for citizen accounts.");
                redirectAttributes.addFlashAttribute("showSignIn", true);
                return "redirect:/admin/login";
            }
            session.setAttribute("user", user);
            redirectAttributes.addFlashAttribute("success", "Welcome back, Admin " + user.getName() + "!");
            return "redirect:/admin-dashboard";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password. Please try again.");
            redirectAttributes.addFlashAttribute("showSignIn", true);
            return "redirect:/admin/login";
        }
    }

    @PostMapping("/admin/register")
    public String processAdminRegister(@RequestParam String name,
                                      @RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String location,
                                      @RequestParam String category,
                                      RedirectAttributes redirectAttributes) {
        try {
            // Validate input parameters
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("Name is required");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("Password is required");
            }
            if (location == null || location.trim().isEmpty()) {
                throw new RuntimeException("Location is required");
            }
            if (category == null || category.trim().isEmpty()) {
                throw new RuntimeException("Specialization category is required");
            }
            
            IssueCategory specialization;
            try {
                // Debug category value
                System.out.println("=== ADMIN REGISTRATION DEBUG ===");
                System.out.println("Received category parameter: '" + category + "'");
                System.out.println("Available categories: " + java.util.Arrays.toString(IssueCategory.values()));
                
                specialization = IssueCategory.valueOf(category.toUpperCase());
                System.out.println("✅ Successfully parsed category: " + specialization);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Failed to parse category: " + category);
                throw new RuntimeException("Invalid specialization category: " + category + ". Valid options are: " + java.util.Arrays.toString(IssueCategory.values()));
            }
            
            userService.registerAdminUser(name.trim(), email.trim(), password, UserRole.ADMIN, location.trim(), specialization);
            redirectAttributes.addFlashAttribute("success", "Admin registration successful! Please sign in.");
            redirectAttributes.addFlashAttribute("showSignIn", true);
            return "redirect:/admin/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("showSignUp", true);
            return "redirect:/admin/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed. Please try again.");
            redirectAttributes.addFlashAttribute("showSignUp", true);
            return "redirect:/admin/login";
        }
    }
}
