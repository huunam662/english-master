package com.example.englishmaster_be.domain.news.comment.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.domain.news.comment.dto.res.*;
import com.example.englishmaster_be.domain.news.comment.dto.view.ICountReplyCommentView;
import com.example.englishmaster_be.domain.news.comment.dto.view.IVotesCommentView;
import com.example.englishmaster_be.domain.news.comment.dto.req.CreateCmToCommentReq;
import com.example.englishmaster_be.domain.news.comment.dto.req.CreateNewsCommentReq;
import com.example.englishmaster_be.domain.news.comment.dto.req.UpdateCommentReq;
import com.example.englishmaster_be.domain.news.comment.mapper.CommentMapper;
import com.example.englishmaster_be.domain.news.comment.model.CommentEntity;
import com.example.englishmaster_be.domain.news.news.service.INewsService;
import com.example.englishmaster_be.domain.news.comment.repository.CommentJdbcRepository;
import com.example.englishmaster_be.domain.news.comment.repository.CommentRepository;
import com.example.englishmaster_be.domain.news.news.model.NewsEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j(topic = "COMMENT-SERVICE")
@Service
public class CommentService implements ICommentService {

    private final IUserService userService;
    private final INewsService newsService;
    private final CommentRepository commentRepository;
    private final CommentJdbcRepository commentJdbcRepository;

    @Lazy
    public CommentService(IUserService userService, INewsService newsService, CommentRepository commentRepository, CommentJdbcRepository commentJdbcRepository) {
        this.userService = userService;
        this.newsService = newsService;
        this.commentRepository = commentRepository;
        this.commentJdbcRepository = commentJdbcRepository;
    }

    @Override
    public CommentEntity getCommentById(UUID id) {

        return commentRepository.findByCommentId(id)
                .orElseThrow(
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "Comment not found.")
                );
    }

    @Override
    public CommentEntity getCommentInfoById(UUID id) {

        return commentRepository.findCommentInfoByCommentId(id)
                .orElseThrow(
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "Comment not found.")
                );
    }

    @Override
    public List<CommentNewsRes> getNewsComments(UUID newsId, Integer stepLoad, Integer sizeLoad) {

        if(stepLoad < 1)
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Step must greater than or equals 1.");

        if(sizeLoad < 1)
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Size must greater than or equals 1.");

        Pageable pageable = PageRequest.of(stepLoad - 1, sizeLoad);

        Page<CommentEntity> pageResult = commentRepository.findAllCommentsByNewsId(newsId, pageable);

        List<CommentEntity> comments = pageResult.getContent();

        List<UUID> commentIds = comments.stream().map(CommentEntity::getCommentId).toList();

        List<IVotesCommentView> countVotesComment = commentRepository.countVotesCommentIn(commentIds);

        List<ICountReplyCommentView> countReplyComment = commentRepository.countReplyCommentIn(commentIds);

        Map<UUID, Integer> commentCountVotesGroup = countVotesComment.stream()
                .collect(Collectors.toMap(
                        IVotesCommentView::getCommentId,
                        IVotesCommentView::getCountVotes
                ));
        Map<UUID, Integer> commentCountReplyGroup = countReplyComment.stream()
                .collect(Collectors.toMap(
                        ICountReplyCommentView::getCommentId,
                        ICountReplyCommentView::getCountReplies
                ));

        return comments.stream().map(
            comment -> {
                CommentNewsRes commentNewsResponse = CommentMapper.INSTANCE.toCommentNewsResponse(comment);
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
    public CommentNewsKeyRes commentToNews(CreateNewsCommentReq request) {

        NewsEntity news = newsService.getNewsById(request.getNewsId());

        CommentEntity commentToNews = new CommentEntity();
        commentToNews.setIsCommentParent(true);
        commentToNews.setContent(request.getCommentContent());
        commentToNews.setNews(news);

        commentToNews = commentRepository.save(commentToNews);

        CommentNewsKeyRes commentNewsKeyRes = new CommentNewsKeyRes();
        commentNewsKeyRes.setNewsId(news.getNewsId());
        commentNewsKeyRes.setCommentId(commentToNews.getCommentId());

        return commentNewsKeyRes;
    }

    @Transactional
    @Override
    public CommentKeyRes updateComment(UpdateCommentReq request) {

        UserEntity userUpdate = userService.currentUser();

        CommentEntity comment = getCommentById(request.getCommentId());

        UserEntity userComment = comment.getUserComment();

        if(userComment == null || !userComment.getUserId().equals(userUpdate.getUserId()))
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User current is not owner of this comment.");

        comment.setContent(request.getCommentContent());

        commentJdbcRepository.updateComment(comment);

        return new CommentKeyRes(comment.getCommentId());
    }

    @Transactional
    @Override
    public void deleteComment(UUID id) {

        UserEntity userDelete = userService.currentUser();

        CommentEntity comment = getCommentById(id);

        UserEntity userComment = comment.getUserComment();

        if(userComment == null || !userComment.getUserId().equals(userDelete.getUserId()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "User current is not owner of this comment.");

        commentRepository.delete(comment);
    }

    @Transactional
    @Override
    public CmToCommentNewsKeyRes commentToAnyComment(CreateCmToCommentReq request) {

        UserEntity userComment = null;

        try {
            userComment = userService.currentUser();
        }
        catch (Exception e){
            log.error(e.getMessage());
        }

        CommentEntity commentOwner = getCommentById(request.getCommentOwnerId());

        CommentEntity commentParent = commentOwner.getIsCommentParent() ? commentOwner : commentOwner.getCommentParent();

        CommentEntity comment = new CommentEntity();
        comment.setIsCommentParent(false);
        comment.setContent(request.getCommentContent());
        comment.setCommentParent(commentParent);
        comment.setUserComment(userComment);
        comment.setToOwnerComment(commentOwner.getIsCommentParent() ? null : commentOwner.getUserComment());
        comment = commentRepository.save(comment);

        CmToCommentNewsKeyRes commentNewsKeyRes = new CmToCommentNewsKeyRes();
        commentNewsKeyRes.setCommentId(comment.getCommentId());
        commentNewsKeyRes.setCommentParentId(commentParent.getCommentId());
        commentNewsKeyRes.setToOwnerCommentId(comment.getToOwnerComment() != null ? comment.getToOwnerComment().getUserId() : null);

        return commentNewsKeyRes;
    }

    @Override
    public List<CommentChildRes> getCommentsChild(UUID commentParentId, Integer stepLoad, Integer sizeLoad) {

        Assert.notNull(commentParentId, "Comment parent id is required.");

        if(stepLoad < 1)
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Step must greater than or equals 1.");

        if(sizeLoad < 1)
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Size must greater than or equals 1.");

        Pageable pageable = PageRequest.of(stepLoad - 1, sizeLoad);

        Page<CommentEntity> pageResult = commentRepository.findAllCommentsChildByCommentParentId(commentParentId, pageable);

        List<CommentEntity> comments = pageResult.getContent();

        List<UUID> commentIds = comments.stream().map(CommentEntity::getCommentId).toList();

        List<IVotesCommentView> countVotesComment = commentRepository.countVotesCommentIn(commentIds);

        Map<UUID, Integer> countVotesCommentGroup = countVotesComment.stream().collect(
                Collectors.toMap(
                        IVotesCommentView::getCommentId,
                        IVotesCommentView::getCountVotes
                )
        );

        return comments.stream().map(
                comment -> {
                    CommentChildRes commentChild = CommentMapper.INSTANCE.toCommentChildResponse(comment);
                    commentChild.setNumberOfVotes(countVotesCommentGroup.getOrDefault(comment.getCommentId(), 0));
                    return commentChild;
                }
        ).toList();
    }

    @Transactional
    @Override
    public CommentKeyRes votesToComment(UUID commentId) {

        UserEntity userVote = userService.currentUser();

        CommentEntity comment = getCommentById(commentId);

        commentJdbcRepository.insertCommentsVotes(comment, userVote);

        return new CommentKeyRes(comment.getCommentId());
    }

    @Transactional
    @Override
    public CommentKeyRes unVotesToComment(UUID commentId) {

        UserEntity userVote = userService.currentUser();

        CommentEntity comment = getCommentById(commentId);

        commentJdbcRepository.deleteCommentsVotes(comment, userVote);

        return new CommentKeyRes(comment.getCommentId());
    }
}
