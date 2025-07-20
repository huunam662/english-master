package com.example.englishmaster_be.domain.news.comment.service;


import com.example.englishmaster_be.domain.news.comment.dto.req.CreateCmToCommentReq;
import com.example.englishmaster_be.domain.news.comment.dto.req.CreateNewsCommentReq;
import com.example.englishmaster_be.domain.news.comment.dto.req.UpdateCommentReq;
import com.example.englishmaster_be.domain.news.comment.dto.res.*;
import com.example.englishmaster_be.domain.news.comment.model.CommentEntity;
import java.util.List;
import java.util.UUID;

public interface ICommentService {

    CommentEntity getCommentById(UUID id);

    CommentEntity getCommentInfoById(UUID id);

    List<CommentNewsRes> getNewsComments(UUID newsId, Integer stepLoad, Integer sizeLoad);

    CommentNewsKeyRes commentToNews(CreateNewsCommentReq request);

    CommentKeyRes updateComment(UpdateCommentReq request);

    void deleteComment(UUID id);

    CmToCommentNewsKeyRes commentToAnyComment(CreateCmToCommentReq request);

    List<CommentChildRes> getCommentsChild(UUID commentParentId, Integer stepLoad, Integer sizeLoad);

    CommentKeyRes votesToComment(UUID commentId);

    CommentKeyRes unVotesToComment(UUID commentId);
}
