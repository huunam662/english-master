package com.example.englishmaster_be.domain.comment.service;


import com.example.englishmaster_be.domain.comment.dto.request.CreateCmToCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.request.CreateNewsCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.request.UpdateCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.response.*;
import com.example.englishmaster_be.model.comment.CommentEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface ICommentService {

    CommentEntity getCommentById(UUID id);

    CommentEntity getCommentInfoById(UUID id);

    List<CommentNewsResponse> getNewsComments(UUID newsId, Integer stepLoad, Integer sizeLoad);

    CommentNewsKeyResponse commentToNews(CreateNewsCommentRequest request);

    CommentKeyResponse updateComment(UpdateCommentRequest request);

    void deleteComment(UUID id);

    CmToCommentNewsKeyResponse commentToAnyComment(CreateCmToCommentRequest request);

    List<CommentChildResponse> getCommentsChild(UUID commentParentId, Integer stepLoad, Integer sizeLoad);

    CommentKeyResponse votesToComment(UUID commentId);

    CommentKeyResponse unVotesToComment(UUID commentId);
}
