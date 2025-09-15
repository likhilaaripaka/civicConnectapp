package com.civicconnect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solved_issues")
public class SolvedIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private IssueCategory category;

    private String location;

    private String imagePath;

    private String videoPath;

    private LocalDateTime originalCreatedAt;

    private LocalDateTime solvedAt;

    private String solvedBy;

    public SolvedIssue() {}

    public SolvedIssue(Issue issue, String solvedBy) {
        this.title = issue.getTitle();
        this.description = issue.getDescription();
        this.category = issue.getCategory();
        this.location = issue.getLocation();
        this.imagePath = issue.getImagePath();
        this.videoPath = issue.getVideoPath();
        this.originalCreatedAt = issue.getCreatedAt();
        this.solvedAt = LocalDateTime.now();
        this.solvedBy = solvedBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueCategory getCategory() {
        return category;
    }

    public void setCategory(IssueCategory category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public LocalDateTime getOriginalCreatedAt() {
        return originalCreatedAt;
    }

    public void setOriginalCreatedAt(LocalDateTime originalCreatedAt) {
        this.originalCreatedAt = originalCreatedAt;
    }

    public LocalDateTime getSolvedAt() {
        return solvedAt;
    }

    public void setSolvedAt(LocalDateTime solvedAt) {
        this.solvedAt = solvedAt;
    }

    public String getSolvedBy() {
        return solvedBy;
    }

    public void setSolvedBy(String solvedBy) {
        this.solvedBy = solvedBy;
    }
}
