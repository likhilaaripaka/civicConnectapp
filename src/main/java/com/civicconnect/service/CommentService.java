package com.civicconnect.service;

import com.civicconnect.model.Comment;
import com.civicconnect.model.Issue;
import com.civicconnect.model.User;
import com.civicconnect.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    public Comment addComment(String content, User user, Issue issue) {
        Comment comment = new Comment(content, user, issue);
        return commentRepository.save(comment);
    }
    
    public List<Comment> getCommentsByIssue(Issue issue) {
        return commentRepository.findByIssueOrderByCreatedAtAsc(issue);
    }
    
    public long getCommentCount(Issue issue) {
        return commentRepository.countByIssue(issue);
    }
}
