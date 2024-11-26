package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.model.Comment;
import com.example.englishmaster_be.repository.*;
import com.example.englishmaster_be.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public boolean checkCommentParent(Comment comment) {
        return commentRepository.existsByCommentParent(comment);
    }

    @Override
    public List<Comment> findAllByCommentParent(Comment commentParent) {
        if(commentRepository.existsByCommentParent(commentParent)){
            return commentRepository.findAllByCommentParent(commentParent).stream().toList();
        }
        return null;
    }

    @Override
    public Comment findCommentToId(UUID commentID) {
        return commentRepository.findByCommentId(commentID).orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentID));
    }
}
