package com.civicconnect.service;

import com.civicconnect.model.Issue;
import com.civicconnect.model.IssueCategory;
import com.civicconnect.model.User;
import com.civicconnect.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IssueService {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private IssueRepository issueRepository;

    // ✅ Get all unsolved issues (latest first)
    public List<Issue> getAllIssues() {
        return issueRepository.findByIsSolvedFalseAndIsDeletedFalseOrderByCreatedAtDesc();
    }

    // ✅ Get issues by category (unsolved)
    public List<Issue> getIssuesByCategory(IssueCategory category) {
        return issueRepository.findByCategoryAndIsSolvedFalseAndIsDeletedFalseOrderByCreatedAtDesc(category);
    }

    // ✅ Get issues by user (all issues created by specific user)
    public List<Issue> getIssuesByUser(User user) {
        return issueRepository.findByUserAndIsDeletedFalseOrderByCreatedAtDesc(user);
    }

    // ✅ Get issues NOT in admin's specialization category (other categories)
    public List<Issue> getIssuesExcludingCategory(IssueCategory excludeCategory) {
        try {
            return issueRepository.findByCategoryNotAndIsSolvedFalseAndIsDeletedFalseOrderByCreatedAtDesc(excludeCategory);
        } catch (Exception e) {
            // Fallback: get all issues and filter manually if repository method fails
            return getAllIssues().stream()
                    .filter(issue -> !issue.getCategory().equals(excludeCategory))
                    .toList();
        }
    }

    // ✅ Get issue by ID
    public Issue getIssueById(Long id) {
        return issueRepository.findById(id).orElse(null);
    }

    // ✅ Create new issue (with optional image/video)
    public void createIssue(String title,
                            String description,
                            IssueCategory category,
                            User user,
                            String location,
                            MultipartFile imageFile,
                            MultipartFile videoFile) {

        Issue issue = new Issue(title, description, category, user, location);

        // Handle optional image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                System.out.println("Processing image file: " + imageFile.getOriginalFilename() + " (size: " + imageFile.getSize() + ")");
                String imagePath = saveFile(imageFile);
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    System.out.println("🔧 Setting image path on issue object: " + imagePath);
                    issue.setImagePath(imagePath);
                    System.out.println("✅ Image path set successfully: " + issue.getImagePath());
                } else {
                    System.err.println("❌ Failed to save image file - null or empty path returned");
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to save image file: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No image file provided or file is empty");
        }

        // Handle optional video upload
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                System.out.println("Processing video file: " + videoFile.getOriginalFilename() + " (size: " + videoFile.getSize() + ")");
                String videoPath = saveFile(videoFile);
                if (videoPath != null && !videoPath.trim().isEmpty()) {
                    issue.setVideoPath(videoPath);
                    System.out.println("✅ Video saved successfully with path: " + videoPath);
                } else {
                    System.err.println("❌ Failed to save video file - null or empty path returned");
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to save video file: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No video file provided or file is empty");
        }

        // Save issue and log final state
        System.out.println("🔄 Saving issue to database...");
        System.out.println("📸 Image path before save: " + issue.getImagePath());
        System.out.println("🎥 Video path before save: " + issue.getVideoPath());
        
        Issue savedIssue = issueRepository.save(issue);
        System.out.println("💾 Issue saved with ID: " + savedIssue.getId());
        System.out.println("📸 Final image path after save: " + savedIssue.getImagePath());
        System.out.println("🎥 Final video path after save: " + savedIssue.getVideoPath());
        
        // Verify by fetching from database
        Issue fetchedIssue = issueRepository.findById(savedIssue.getId()).orElse(null);
        if (fetchedIssue != null) {
            System.out.println("🔍 Verification - fetched from DB:");
            System.out.println("📸 Image path from DB: " + fetchedIssue.getImagePath());
            System.out.println("🎥 Video path from DB: " + fetchedIssue.getVideoPath());
        }
    }

    // ✅ Delete issue by ID
    public void deleteIssue(Long id) {
        issueRepository.deleteById(id);
    }

    // ✅ Mark issue as solved with admin details
    public void markAsSolved(Issue issue, User admin, String solutionDescription, MultipartFile solutionImage) {
        issue.setSolved(true);
        issue.setSolvedAt(LocalDateTime.now());
        issue.setSolvedByAdmin(admin);
        issue.setSolutionDescription(solutionDescription);
        issue.setUpdatedAt(LocalDateTime.now());
        
        // Handle solution image upload
        if (solutionImage != null && !solutionImage.isEmpty()) {
            try {
                String solutionImagePath = saveFile(solutionImage);
                if (solutionImagePath != null) {
                    issue.setSolutionImagePath(solutionImagePath);
                }
            } catch (Exception e) {
                System.err.println("Failed to save solution image: " + e.getMessage());
            }
        }
        
        issueRepository.save(issue);
    }

    // ✅ Delete solved issue with admin details
    public void deleteSolvedIssue(Issue issue, User admin, String deletionReason) {
        if (!issue.isSolved()) {
            throw new RuntimeException("Only solved issues can be deleted");
        }
        
        issue.setDeleted(true);
        issue.setDeletedAt(LocalDateTime.now());
        issue.setDeletedByAdmin(admin);
        issue.setDeletionReason(deletionReason);
        issue.setUpdatedAt(LocalDateTime.now());
        
        issueRepository.save(issue);
    }

    // ✅ Get solved issues
    public List<Issue> getSolvedIssues() {
        return issueRepository.findByIsSolvedTrueAndIsDeletedFalseOrderByUpdatedAtDesc();
    }

    // ✅ Get deleted issues
    public List<Issue> getDeletedIssues() {
        return issueRepository.findByIsDeletedTrueOrderByDeletedAtDesc();
    }

    // ✅ Helper: Save uploaded file and return its path
    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            System.out.println("❌ File is empty, not saving");
            return null;
        }

        // Create uploads directory if it doesn't exist
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
            System.out.println("✅ Created uploads directory: " + uploadDir.toAbsolutePath());
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            System.out.println("❌ Original filename is null or empty");
            return null;
        }
        
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadDir.resolve(uniqueFilename);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ File saved successfully:");
            System.out.println("   - Original: " + originalFilename);
            System.out.println("   - Saved as: " + uniqueFilename);
            System.out.println("   - Full path: " + filePath.toAbsolutePath());
            System.out.println("   - File size: " + Files.size(filePath) + " bytes");
            
            // Verify file exists
            if (Files.exists(filePath)) {
                System.out.println("✅ File verification successful");
                return uniqueFilename;
            } else {
                System.out.println("❌ File verification failed - file does not exist after save");
                return null;
            }
        } catch (IOException e) {
            System.out.println("❌ Error saving file: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
