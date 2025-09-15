package com.civicconnect.repository;

import com.civicconnect.model.Support;
import com.civicconnect.model.User;
import com.civicconnect.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {
    Optional<Support> findByUserAndIssue(User user, Issue issue);
    boolean existsByUserAndIssue(User user, Issue issue);
    long countByIssue(Issue issue);
}
