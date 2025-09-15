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
public class CitizenController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SupportService supportService;


    // ✅ Show solved issues for citizens
    @GetMapping("/citizen/solved-issues")
    public String showSolvedIssues(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.CITIZEN) {
            return "redirect:/user/login";
        }

        List<Issue> solvedIssues = issueService.getSolvedIssues();
        model.addAttribute("solvedIssues", solvedIssues);
        return "citizen-solved-issues";
    }

    // ✅ Show citizen dashboard with search functionality
    @GetMapping("/citizen-dashboard")
    public String citizenDashboard(@RequestParam(required = false) IssueCategory category,
                                   @RequestParam(required = false) String location,
                                   Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.CITIZEN) {
            return "redirect:/user/login";
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
            issues = issueService.getAllIssues().stream()
                    .filter(issue -> issue.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .toList();
        } else {
            // No filters - show all issues
            issues = issueService.getAllIssues();
        }

        model.addAttribute("issues", issues);
        model.addAttribute("user", user);
        model.addAttribute("categories", IssueCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchLocation", location);
        return "citizen-dashboard";
    }

    // ✅ Show user's own issues
    @GetMapping("/citizen/my-issues")
    public String showMyIssues(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.CITIZEN) {
            return "redirect:/user/login";
        }

        List<Issue> myIssues = issueService.getIssuesByUser(user);
        model.addAttribute("myIssues", myIssues);
        model.addAttribute("user", user);
        return "citizen-my-issues";
    }

    // ✅ Issues by category
    @GetMapping("/issues/category/{category}")
    public String issuesByCategory(@PathVariable IssueCategory category, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Issue> issues = issueService.getIssuesByCategory(category);
        model.addAttribute("issues", issues);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", IssueCategory.values());

        return (user.getRole() == UserRole.ADMIN) ? "admin-dashboard" : "citizen-dashboard";
    }

    // ✅ View issue detail
    @GetMapping("/issue/{id}")
    public String viewIssue(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Optional<Issue> optionalIssue = Optional.ofNullable(issueService.getIssueById(id));
        if (optionalIssue.isEmpty()) {
            return (user.getRole() == UserRole.ADMIN) ? "redirect:/admin-dashboard" : "redirect:/citizen-dashboard";
        }

        Issue issue = optionalIssue.get();
        List<Comment> comments = commentService.getCommentsByIssue(issue);
        long supportCount = supportService.getSupportCount(issue);
        boolean hasSupported = supportService.hasUserSupported(user, issue);

        // Debug media paths and user access
        System.out.println("=== ISSUE VIEW DEBUG ===");
        System.out.println("Issue ID: " + issue.getId());
        System.out.println("Issue Creator: " + issue.getUser().getName() + " (ID: " + issue.getUser().getId() + ")");
        System.out.println("Current Viewer: " + user.getName() + " (ID: " + user.getId() + ")");
        System.out.println("Image Path: " + issue.getImagePath());
        System.out.println("Video Path: " + issue.getVideoPath());
        System.out.println("Is same user? " + issue.getUser().getId().equals(user.getId()));

        model.addAttribute("issue", issue);
        model.addAttribute("comments", comments);
        model.addAttribute("supportCount", supportCount);
        model.addAttribute("hasSupported", hasSupported);

        return "issue-detail";
    }

    // ✅ Add comment
    @PostMapping("/issue/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Optional<Issue> optionalIssue = Optional.ofNullable(issueService.getIssueById(id));
        optionalIssue.ifPresent(issue -> commentService.addComment(content, user, issue));

        return "redirect:/issue/" + id;
    }

    // ✅ Toggle support
    @PostMapping("/issue/{id}/support")
    public String toggleSupport(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Optional<Issue> optionalIssue = Optional.ofNullable(issueService.getIssueById(id));
        optionalIssue.ifPresent(issue -> supportService.toggleSupport(user, issue));

        return "redirect:/issue/" + id;
    }

    // ✅ New issue form
    @GetMapping("/new-issue")
    public String newIssuePage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("categories", IssueCategory.values());
        return "new-issue";
    }

    // ✅ Create new issue
    @PostMapping("/new-issue")
    public String createIssue(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam IssueCategory category,
                              @RequestParam String location,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";

        try {
            // Debug file upload info
            System.out.println("=== FILE UPLOAD DEBUG ===");
            System.out.println("Title: " + title);
            System.out.println("Description: " + description);
            System.out.println("Category: " + category);
            System.out.println("Location: " + location);
            System.out.println("Image file: " + (imageFile != null ? imageFile.getOriginalFilename() + " (" + imageFile.getSize() + " bytes)" : "null"));
            System.out.println("Video file: " + (videoFile != null ? videoFile.getOriginalFilename() + " (" + videoFile.getSize() + " bytes)" : "null"));
            System.out.println("Image file empty? " + (imageFile != null ? imageFile.isEmpty() : "null"));
            System.out.println("Video file empty? " + (videoFile != null ? videoFile.isEmpty() : "null"));
            
            issueService.createIssue(title, description, category, user, location, imageFile, videoFile);
            redirectAttributes.addFlashAttribute("success", "Issue created successfully!");
        } catch (Exception e) {
            System.out.println("❌ ERROR creating issue: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to create issue: " + e.getMessage());
            e.printStackTrace();
        }

        return (user.getRole() == UserRole.ADMIN) ? "redirect:/admin-dashboard" : "redirect:/citizen-dashboard";
    }
}
