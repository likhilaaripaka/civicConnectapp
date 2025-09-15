package com.civicconnect.repository;

import com.civicconnect.model.SolvedIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolvedIssueRepository extends JpaRepository<SolvedIssue, Long> {
    List<SolvedIssue> findByOrderBySolvedAtDesc();
    List<SolvedIssue> findByLocationContainingOrderBySolvedAtDesc(String location);
}
