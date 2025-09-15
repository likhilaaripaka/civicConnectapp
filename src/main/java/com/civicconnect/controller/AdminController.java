package com.civicconnect.controller;

import com.civicconnect.model.*;
import com.civicconnect.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SupportService supportService;
    
    @Autowired
    private UserService userService;

    // ✅ Admin Dashboard - Shows issues based on admin's specialization
    @GetMapping("/admin-dashboard")
    public String adminDashboard(@RequestParam(required = false) IssueCategory category,
                                 @RequestParam(required = false) String location,
                                 Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/admin/login";
        }

        List<Issue> issues;
        
        // Apply search filters
        if (category != null && location != null && !location.trim().isEmpty()) {
            // Filter by both category and location
            issues = issueService.getIssuesByCategory(category).stream()
                    .filter(issue -> issue.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .toList();
        } else if (category != null) {
            // Filter by category only
            issues = issueService.getIssuesByCategory(category);
        } else if (location != null && !location.trim().isEmpty()) {
            // Filter by location only
            List<Issue> allIssues = user.getSpecialization() != null ? 
                    issueService.getIssuesByCategory(user.getSpecialization()) : 
                    issueService.getAllIssues();
            issues = allIssues.stream()
                    .filter(issue -> issue.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .toList();
        } else {
            // No filters - show admin's specialized issues or all issues
            if (user.getSpecialization() != null) {
                issues = issueService.getIssuesByCategory(user.getSpecialization());
            } else {
                issues = issueService.getAllIssues();
            }
        }

        // Get all admin users for admin details section
        List<User> adminUsers = userService.getAllAdminUsers();
        
        model.addAttribute("issues", issues);
        model.addAttribute("user", user);
        model.addAttribute("adminUsers", adminUsers);
        model.addAttribute("categories", IssueCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchLocation", location);
        model.addAttribute("showingOtherIssues", false);
        
        // Debug admin specialization and issue details
        System.out.println("=== ADMIN DASHBOARD DEBUG ===");
        System.out.println("Admin: " + user.getName());
        System.out.println("Admin Specialization: " + user.getSpecialization());
        System.out.println("Issues count: " + issues.size());
        for (Issue issue : issues) {
            System.out.println("Issue ID: " + issue.getId() + ", Image Path: '" + issue.getImagePath() + "'");
        }
        
        return "admin-dashboard";
    }
    

    // ✅ Admin Dashboard - Other Issues (not in admin's specialization)
    @GetMapping("/admin/dashboard/other-issues")
    public String adminOtherIssues(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/admin/login";
        }

        try {
            // Show ALL issues regardless of admin's specialization
            List<Issue> issues = issueService.getAllIssues();
            model.addAttribute("issueCount", issues.size());
            
            model.addAttribute("issues", issues);
            model.addAttribute("user", user); // ✅ Add user to model
            model.addAttribute("categories", IssueCategory.values());
            model.addAttribute("showingOtherIssues", true);
            return "admin-dashboard";
        } catch (Exception e) {
            System.err.println("Error in adminOtherIssues: " + e.getMessage());
            e.printStackTrace();
            // Fallback to regular dashboard if there's an error
            return "redirect:/admin-dashboard";
        }
    }

    // ✅ View issue detail (admin perspective)
    @GetMapping("/admin/issue/{id}")
    public String viewIssue(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/login";
        }

        Optional<Issue> optionalIssue = Optional.ofNullable(issueService.getIssueById(id));
        if (optionalIssue.isEmpty()) {
            return "redirect:/admin-dashboard";
        }

        Issue issue = optionalIssue.get();
        
        // Debug image path for this specific issue
        System.out.println("=== ISSUE DETAIL DEBUG ===");
        System.out.println("Issue ID: " + issue.getId());
        System.out.println("Issue Title: " + issue.getTitle());
        System.out.println("Image Path: '" + issue.getImagePath() + "'");
        System.out.println("Video Path: '" + issue.getVideoPath() + "'");
        
        List<Comment> comments = commentService.getCommentsByIssue(issue);
        long supportCount = supportService.getSupportCount(issue);

        model.addAttribute("issue", issue);
        model.addAttribute("comments", comments);
        model.addAttribute("supportCount", supportCount);

        return "admin-issue-detail";  // ✅ dedicated admin template with enhanced controls
    }

    // ✅ View issue detail (for admins)
    @GetMapping("/admin/issue/{id}/view")
    public String viewIssueAdmin(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/admin/login";
        }

        Issue issue = issueService.getIssueById(id);
        if (issue == null) {
            return "redirect:/admin-dashboard";
        }

        // Debug media paths and admin access
        System.out.println("=== ADMIN ISSUE VIEW DEBUG ===");
        System.out.println("Issue ID: " + issue.getId());
        System.out.println("Issue Creator: " + issue.getUser().getName() + " (ID: " + issue.getUser().getId() + ")");
        System.out.println("Admin Viewer: " + user.getName() + " (ID: " + user.getId() + ")");
        System.out.println("Image Path: " + issue.getImagePath());
        System.out.println("Video Path: " + issue.getVideoPath());

        model.addAttribute("issue", issue);
        return "issue-detail";
    }

    // ✅ Show solve issue form
    @GetMapping("/admin/issue/{id}/solve")
    public String showSolveForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/admin/login";
        }

        Optional<Issue> optionalIssue = Optional.ofNullable(issueService.getIssueById(id));
        if (optionalIssue.isEmpty()) {
            return "redirect:/admin-dashboard";
        }

        model.addAttribute("issue", optionalIssue.get());
        return "solve-issue";
    }

    // ✅ Mark issue as solved with solution details
    @PostMapping("/admin/issue/{id}/solve")
    public String solveIssue(@PathVariable Long id,
                             @RequestParam String solutionDescription,
                             @RequestParam(required = false) MultipartFile solutionImage,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/admin/login";
        }

        Optional<Issue> optionalIssue = Optional.ofNullable(issueService.getIssueById(id));
        if (optionalIssue.isPresent()) {
            issueService.markAsSolved(optionalIssue.get(), user, solutionDescription, solutionImage);
            redirectAttributes.addFlashAttribute("success", "Issue marked as solved with solution details.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Issue not found.");
        }

        return "redirect:/admin-dashboard";
    }

    // ✅ Show delete issue form (only for solved issues)
    @GetMapping("/admin/issue/{id}/delete")
    public String showDeleteForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/admin/login";
        }

        Optional<Issue> optionalIssue = Optional.ofNullable(issueService.getIssueById(id));
        if (optionalIssue.isEmpty()) {
            return "redirect:/admin-dashboard";
        }

        Issue issue = optionalIssue.get();
        if (!issue.isSolved()) {
            return "redirect:/admin-dashboard";
        }

        model.addAttribute("issue", issue);
        return "delete-issue";
    }

    // ✅ Delete solved issue with deletion reason
    @PostMapping("/admin/issue/{id}/delete")
    public String deleteIssue(@PathVariable Long id,
                              @RequestParam String deletionReason,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/admin/login";
        }

        try {
            Optional<Issue> optionalIssue = Optional.ofNullable(issueService.getIssueById(id));
            if (optionalIssue.isPresent()) {
                issueService.deleteSolvedIssue(optionalIssue.get(), user, deletionReason);
                redirectAttributes.addFlashAttribute("success", "Solved issue deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Issue not found.");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin-dashboard";
    }

    // ✅ Show solved issues
    @GetMapping("/admin/solved-issues")
    public String showSolvedIssues(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/admin/login";
        }

        List<Issue> solvedIssues = issueService.getSolvedIssues();
        model.addAttribute("solvedIssues", solvedIssues);
        return "admin-solved-issues";
    }

    // ✅ Show deleted issues
    @GetMapping("/admin/deleted-issues")
    public String showDeletedIssues(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/admin/login";
        }

        List<Issue> deletedIssues = issueService.getDeletedIssues();
        model.addAttribute("deletedIssues", deletedIssues);
        return "admin-deleted-issues";
    }
}
