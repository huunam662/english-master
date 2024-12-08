package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Comment.SaveCommentDTO;
import com.example.englishmaster_be.DTO.Comment.UpdateCommentDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface ICommentService {

    void save(Comment comment);

    void deleteComment(Comment comment);

    Comment findCommentToId(UUID commentId);

    boolean checkCommentParent(Comment comment);

    List<Comment> findAllByCommentParent(Comment commentParent);

    List<CommentResponse> getListCommentByCommentId(UUID commentId);

    CommentResponse createCommentToTopic(UUID topicId, SaveCommentDTO createCommentDTO);

    CommentResponse createCommentToPost(UUID postId, SaveCommentDTO createCommentDTO);

    CommentResponse createCommentToComment(UUID commentId, SaveCommentDTO createCommentDTO);

    CommentResponse updateComment(UpdateCommentDTO updateCommentDTO);

    void deleteComment(UUID commentId);
}
