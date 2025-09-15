package com.civicconnect.controller;

import com.civicconnect.model.User;
import com.civicconnect.model.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/features")
    public String features() {
        return "features";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        if (user.getRole() == UserRole.ADMIN) {
            return "redirect:/admin-dashboard";
        } else {
            return "redirect:/citizen-dashboard";
        }
    }
}
