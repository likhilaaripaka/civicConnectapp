package com.civicconnect.repository;

import com.civicconnect.model.Issue;
import com.civicconnect.model.IssueCategory;
import com.civicconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByIsSolvedFalseAndIsDeletedFalseOrderByCreatedAtDesc();
    List<Issue> findByCategoryAndIsSolvedFalseAndIsDeletedFalseOrderByCreatedAtDesc(IssueCategory category);
    List<Issue> findByCategoryNotAndIsSolvedFalseAndIsDeletedFalseOrderByCreatedAtDesc(IssueCategory category);
    List<Issue> findByIsSolvedTrueAndIsDeletedFalseOrderByUpdatedAtDesc();
    List<Issue> findByIsDeletedTrueOrderByDeletedAtDesc();
    List<Issue> findByUserAndIsDeletedFalseOrderByCreatedAtDesc(User user);
}
