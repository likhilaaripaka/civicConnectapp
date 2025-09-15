package com.civicconnect.service;

import com.civicconnect.model.Support;
import com.civicconnect.model.Issue;
import com.civicconnect.model.User;
import com.civicconnect.repository.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupportService {
    
    @Autowired
    private SupportRepository supportRepository;
    
    public boolean toggleSupport(User user, Issue issue) {
        if (supportRepository.existsByUserAndIssue(user, issue)) {
            // Remove support
            Support support = supportRepository.findByUserAndIssue(user, issue).orElse(null);
            if (support != null) {
                supportRepository.delete(support);
                return false; // Support removed
            }
        } else {
            // Add support
            Support support = new Support(user, issue);
            supportRepository.save(support);
            return true; // Support added
        }
        return false;
    }
    
    public boolean hasUserSupported(User user, Issue issue) {
        return supportRepository.existsByUserAndIssue(user, issue);
    }
    
    public long getSupportCount(Issue issue) {
        return supportRepository.countByIssue(issue);
    }
}
