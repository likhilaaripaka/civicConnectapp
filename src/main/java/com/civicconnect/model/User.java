package com.civicconnect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String location;

    @Enumerated(EnumType.STRING)
    private IssueCategory specialization; // For admin users - their area of expertise

    public User() {}

    public User(String name, String email, String password, UserRole role, String location) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.location = location;
    }

    public User(String name, String email, String password, UserRole role, String location, IssueCategory specialization) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.location = location;
        this.specialization = specialization;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public IssueCategory getSpecialization() {
        return specialization;
    }

    public void setSpecialization(IssueCategory specialization) {
        this.specialization = specialization;
    }
}
