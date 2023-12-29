package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Comment;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public boolean checkCommentParent(Comment comment) {
        return commentRepository.existsByCommentParent(comment);
    }
}
