package com.civicconnect.service;

import com.civicconnect.model.User;
import com.civicconnect.model.UserRole;
import com.civicconnect.model.IssueCategory;
import com.civicconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(String name, String email, String password, UserRole role, String location) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(name, email, encodedPassword, role, location);
        return userRepository.save(user);
    }
    
    public User registerAdminUser(String name, String email, String password, UserRole role, String location, IssueCategory specialization) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(name, email, encodedPassword, role, location, specialization);
        return userRepository.save(user);
    }
    
    public User loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        throw new RuntimeException("Invalid email or password");
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    public List<User> getAllAdminUsers() {
        return userRepository.findByRole(UserRole.ADMIN);
    }
}
