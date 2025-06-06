package com.example.englishmaster_be.domain.comment.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.comment.dto.request.CreateCmToCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.request.CreateNewsCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.request.UpdateCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.response.CmToCommentNewsKeyResponse;
import com.example.englishmaster_be.domain.comment.dto.response.CommentChildResponse;
import com.example.englishmaster_be.domain.comment.dto.response.CommentKeyResponse;
import com.example.englishmaster_be.domain.comment.dto.response.CommentNewsKeyResponse;
import com.example.englishmaster_be.domain.news.service.INewsService;
import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.comment.CommentJdbcRepository;
import com.example.englishmaster_be.model.comment.CommentRepository;
import com.example.englishmaster_be.model.news.NewsEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.domain.user.service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.UUID;

@Slf4j(topic = "COMMENT-SERVICE")
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService implements ICommentService {

    IUserService userService;

    INewsService newsService;

    CommentRepository commentRepository;

    CommentJdbcRepository commentJdbcRepository;

    @Override
    public CommentEntity getCommentById(UUID id) {

        Assert.notNull(id, "Comment id is required.");

        return commentRepository.findByCommentId(id)
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND)
                );
    }

    @Override
    public CommentEntity getCommentInfoById(UUID id) {

        Assert.notNull(id, "Comment id is required.");

        return commentRepository.findCommentInfoByCommentId(id)
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND)
                );
    }

    @Override
    public List<CommentEntity> getNewsComments(UUID newsId, Integer stepLoad, Integer sizeLoad) {

        Assert.notNull(newsId, "News id is required.");

        if(stepLoad < 1)
            throw new ErrorHolder(Error.BAD_REQUEST, "Step must greater than or equals 1.");

        if(sizeLoad < 1)
            throw new ErrorHolder(Error.BAD_REQUEST, "Size must greater than or equals 1.");

        Pageable pageable = PageRequest.of(stepLoad - 1, sizeLoad);

        Page<CommentEntity> pageResult = commentRepository.findAllCommentsByNewsId(newsId, pageable);

        return pageResult.getContent();
    }

    @Transactional
    @Override
    public CommentNewsKeyResponse commentToNews(CreateNewsCommentRequest request) {

        UserEntity userComment = null;

        try{
            userComment = userService.currentUser();
        }
        catch (Exception e){
            log.error(e.getMessage());
        }

        NewsEntity news = newsService.getNewsById(request.getNewsId());

        CommentEntity commentToNews = CommentEntity.builder()
                .commentId(UUID.randomUUID())
                .isCommentParent(true)
                .content(request.getCommentContent())
                .userComment(userComment)
                .news(news)
                .build();

        commentJdbcRepository.insertComment(commentToNews);

        return CommentNewsKeyResponse.builder()
                .newsId(news.getNewsId())
                .commentId(commentToNews.getCommentId())
                .build();
    }

    @Transactional
    @Override
    public CommentKeyResponse updateComment(UpdateCommentRequest request) {

        UserEntity userUpdate = userService.currentUser();

        CommentEntity comment = getCommentById(request.getCommentId());

        UserEntity userComment = comment.getUserComment();

        if(userComment == null || !userComment.getUserId().equals(userUpdate.getUserId()))
            throw new ErrorHolder(Error.UNAUTHORIZED, "User current is not owner of this comment.");

        comment.setContent(request.getCommentContent());

        commentJdbcRepository.updateComment(comment);

        return CommentKeyResponse.builder()
                .commentId(comment.getCommentId())
                .build();
    }

    @Transactional
    @Override
    public void deleteComment(UUID id) {

        Assert.notNull(id, "Comment id is required.");

        UserEntity userDelete = userService.currentUser();

        CommentEntity comment = getCommentById(id);

        UserEntity userComment = comment.getUserComment();

        if(userComment == null || !userComment.getUserId().equals(userDelete.getUserId()))
            throw new ErrorHolder(Error.BAD_REQUEST, "User current is not owner of this comment.");

        commentRepository.delete(comment);
    }

    @Transactional
    @Override
    public CmToCommentNewsKeyResponse commentToAnyComment(CreateCmToCommentRequest request) {

        UserEntity userComment = null;

        try {
            userComment = userService.currentUser();
        }
        catch (Exception e){
            log.error(e.getMessage());
        }

        CommentEntity commentOwner = getCommentById(request.getCommentOwnerId());

        CommentEntity commentParent = commentOwner.getIsCommentParent() ? commentOwner : commentOwner.getCommentParent();

        CommentEntity comment = CommentEntity.builder()
                .commentId(UUID.randomUUID())
                .isCommentParent(false)
                .content(request.getCommentContent())
                .commentParent(commentParent)
                .userComment(userComment)
                .toOwnerComment(commentOwner.getIsCommentParent() ? null : commentOwner.getUserComment())
                .build();

        commentJdbcRepository.insertComment(comment);

        return CmToCommentNewsKeyResponse.builder()
                .commentId(comment.getCommentId())
                .commentParentId(commentParent.getCommentId())
                .toOwnerCommentId(comment.getToOwnerComment() != null ? comment.getToOwnerComment().getUserId() : null)
                .build();
    }

    @Override
    public List<CommentEntity> getCommentsChild(UUID commentParentId, Integer stepLoad, Integer sizeLoad) {

        Assert.notNull(commentParentId, "Comment parent id is required.");

        if(stepLoad < 1)
            throw new ErrorHolder(Error.BAD_REQUEST, "Step must greater than or equals 1.");

        if(sizeLoad < 1)
            throw new ErrorHolder(Error.BAD_REQUEST, "Size must greater than or equals 1.");

        Pageable pageable = PageRequest.of(stepLoad - 1, sizeLoad);

        Page<CommentEntity> pageResult = commentRepository.findAllCommentsChildByCommentParentId(commentParentId, pageable);

        return pageResult.getContent();
    }

    @Transactional
    @Override
    public CommentKeyResponse votesToComment(UUID commentId) {

        UserEntity userVote = userService.currentUser();

        CommentEntity comment = getCommentById(commentId);

        commentJdbcRepository.insertCommentsVotes(comment, userVote);

        return CommentKeyResponse.builder()
                .commentId(comment.getCommentId())
                .build();
    }

    @Transactional
    @Override
    public CommentKeyResponse unVotesToComment(UUID commentId) {

        UserEntity userVote = userService.currentUser();

        CommentEntity comment = getCommentById(commentId);

        commentJdbcRepository.deleteCommentsVotes(comment, userVote);

        return CommentKeyResponse.builder()
                .commentId(comment.getCommentId())
                .build();
    }
}
