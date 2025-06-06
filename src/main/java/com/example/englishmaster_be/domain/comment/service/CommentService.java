package com.example.englishmaster_be.domain.comment.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.comment.dto.projection.ICountReplyCommentProjection;
import com.example.englishmaster_be.domain.comment.dto.projection.IVotesCommentProjection;
import com.example.englishmaster_be.domain.comment.dto.request.CreateCmToCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.request.CreateNewsCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.request.UpdateCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.response.*;
import com.example.englishmaster_be.domain.news.service.INewsService;
import com.example.englishmaster_be.domain.comment.mapper.CommentMapper;
import com.example.englishmaster_be.domain.comment.model.CommentEntity;
import com.example.englishmaster_be.domain.comment.repository.jdbc.CommentJdbcRepository;
import com.example.englishmaster_be.domain.comment.repository.jpa.CommentRepository;
import com.example.englishmaster_be.domain.news.model.NewsEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.service.GenericParameterService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j(topic = "COMMENT-SERVICE")
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService implements ICommentService {

    IUserService userService;

    INewsService newsService;

    CommentRepository commentRepository;

    CommentJdbcRepository commentJdbcRepository;
    private final GenericParameterService parameterBuilder;

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
    public List<CommentNewsResponse> getNewsComments(UUID newsId, Integer stepLoad, Integer sizeLoad) {

        Assert.notNull(newsId, "News id is required.");

        if(stepLoad < 1)
            throw new ErrorHolder(Error.BAD_REQUEST, "Step must greater than or equals 1.");

        if(sizeLoad < 1)
            throw new ErrorHolder(Error.BAD_REQUEST, "Size must greater than or equals 1.");

        Pageable pageable = PageRequest.of(stepLoad - 1, sizeLoad);

        Page<CommentEntity> pageResult = commentRepository.findAllCommentsByNewsId(newsId, pageable);

        List<CommentEntity> comments = pageResult.getContent();

        List<UUID> commentIds = comments.stream().map(CommentEntity::getCommentId).toList();

        List<IVotesCommentProjection> countVotesComment = commentRepository.countVotesCommentIn(commentIds);

        List<ICountReplyCommentProjection> countReplyComment = commentRepository.countReplyCommentIn(commentIds);

        Map<UUID, Integer> commentCountVotesGroup = countVotesComment.stream()
                .collect(Collectors.toMap(
                        IVotesCommentProjection::getCommentId,
                        IVotesCommentProjection::getCountVotes
                ));
        Map<UUID, Integer> commentCountReplyGroup = countReplyComment.stream()
                .collect(Collectors.toMap(
                        ICountReplyCommentProjection::getCommentId,
                        ICountReplyCommentProjection::getCountReplies
                ));

        return comments.stream().map(
            comment -> {
                CommentNewsResponse commentNewsResponse = CommentMapper.INSTANCE.toCommentNewsResponse(comment);
                commentNewsResponse.setNumberOfVotes(
                        commentCountVotesGroup.getOrDefault(comment.getCommentId(), 0)
                );
                commentNewsResponse.setNumberOfCommentsChild(
                        commentCountReplyGroup.getOrDefault(comment.getCommentId(), 0)
                );
                return commentNewsResponse;
            }
        ).toList();
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
    public List<CommentChildResponse> getCommentsChild(UUID commentParentId, Integer stepLoad, Integer sizeLoad) {

        Assert.notNull(commentParentId, "Comment parent id is required.");

        if(stepLoad < 1)
            throw new ErrorHolder(Error.BAD_REQUEST, "Step must greater than or equals 1.");

        if(sizeLoad < 1)
            throw new ErrorHolder(Error.BAD_REQUEST, "Size must greater than or equals 1.");

        Pageable pageable = PageRequest.of(stepLoad - 1, sizeLoad);

        Page<CommentEntity> pageResult = commentRepository.findAllCommentsChildByCommentParentId(commentParentId, pageable);

        List<CommentEntity> comments = pageResult.getContent();

        List<UUID> commentIds = comments.stream().map(CommentEntity::getCommentId).toList();

        List<IVotesCommentProjection> countVotesComment = commentRepository.countVotesCommentIn(commentIds);

        Map<UUID, Integer> countVotesCommentGroup = countVotesComment.stream().collect(
                Collectors.toMap(
                        IVotesCommentProjection::getCommentId,
                        IVotesCommentProjection::getCountVotes
                )
        );

        return comments.stream().map(
                comment -> {
                    CommentChildResponse commentChild = CommentMapper.INSTANCE.toCommentChildResponse(comment);
                    commentChild.setNumberOfVotes(countVotesCommentGroup.getOrDefault(comment.getCommentId(), 0));
                    return commentChild;
                }
        ).toList();
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
