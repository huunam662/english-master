package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.*;

import java.util.List;
import java.util.UUID;

public interface ICommentService {
    void save(Comment comment);
    void deleteComment(Comment comment);

    Comment findCommentToId(UUID commentId);

    boolean checkCommentParent(Comment comment);
    List<Comment> findAllByCommentParent(Comment commentParent);
}
