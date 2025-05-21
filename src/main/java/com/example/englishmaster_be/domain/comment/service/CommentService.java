package com.example.englishmaster_be.domain.comment.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.comment.dto.request.CommentRequest;
import com.example.englishmaster_be.domain.news.service.INewsService;
import com.example.englishmaster_be.mapper.CommentMapper;
import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.comment.CommentRepository;
import com.example.englishmaster_be.model.news.NewsEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.shared.service.ws_message.IWsMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService implements ICommentService {

    SimpMessagingTemplate messagingTemplate;

    CommentRepository commentRepository;

    IUserService userService;

    ITopicService topicService;

    INewsService newsService;

    IWsMessageService wsMessageService;


    @Override
    public boolean checkCommentParent(CommentEntity comment) {

        return commentRepository.existsByCommentParent(comment);
    }

    @Override
    public List<CommentEntity> findAllByCommentParent(CommentEntity commentParent) {
        if(!commentRepository.existsByCommentParent(commentParent))
            return new ArrayList<>();

        return commentRepository.findAllByCommentParent(commentParent);
    }

    @Override
    public CommentEntity getCommentById(UUID commentID) {
        return commentRepository.findByCommentId(commentID)
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Comment not found: " + commentID)
                );
    }

    @Override
    public List<CommentEntity> getListCommentByCommentId(UUID commentId) {

        CommentEntity comment = getCommentById(commentId);

        return findAllByCommentParent(comment);
    }

    @Transactional
    @Override
    public CommentEntity saveCommentToTopic(UUID topicId, CommentRequest commentRequest) {

        UserEntity user = userService.currentUser();

        TopicEntity topic = topicService.getTopicById(topicId);

        CommentEntity comment = CommentEntity.builder()
                .userComment(user)
                .topic(topic)
                .content(commentRequest.getCommentContent())
                .build();

        comment = commentRepository.save(comment);

        return comment;
    }

    @Transactional
    @Override
    public CommentEntity saveCommentToNews(UUID newsId, CommentRequest commentRequest) {

        UserEntity user = userService.currentUser();

        NewsEntity news = newsService.getNewsById(newsId);

        CommentEntity comment = CommentEntity.builder()
                .userComment(user)
                .news(news)
                .content(commentRequest.getCommentContent())
                .build();

        comment = commentRepository.save(comment);

        return comment;
    }

    @Transactional
    @Override
    public CommentEntity saveCommentToComment(UUID commentId, CommentRequest commentRequest) {

        UserEntity user = userService.currentUser();

        CommentEntity commentParent = getCommentById(commentId);

        CommentEntity comment = CommentEntity.builder()
                .userComment(user)
                .commentParent(commentParent)
                .content(commentRequest.getCommentContent())
                .build();

        if(commentParent.getTopic() != null)
            comment.setTopic(commentParent.getTopic());

        comment = commentRepository.save(comment);

        UserEntity userTag = commentParent.getUserComment();

        wsMessageService.sendToUsers(userTag, comment);

        return comment;
    }

    @Transactional
    @Override
    public CommentEntity saveComment(UUID updateCommentId, CommentRequest commentRequest) {

        UserEntity user = userService.currentUser();

        CommentEntity comment = getCommentById(updateCommentId);

        if(!comment.getUserComment().getUserId().equals(user.getUserId()))
            throw new ErrorHolder(Error.BAD_REQUEST, "Don't update Comment");

        comment.setContent(commentRequest.getCommentContent());

        comment  = commentRepository.save(comment);

        messagingTemplate.convertAndSend("/CommentEntity/updateComment/" + updateCommentId.toString(), CommentMapper.INSTANCE.toCommentResponse(comment));

        return comment;
    }

    @Transactional
    @Override
    public void deleteComment(UUID commentId) {

        UserEntity user = userService.currentUser();

        CommentEntity comment = getCommentById(commentId);

        if(!comment.getUserComment().getUserId().equals(user.getUserId()))
            throw new ErrorHolder(Error.BAD_REQUEST, "Don't delete Comment");

        commentRepository.delete(comment);

        messagingTemplate.convertAndSend("/CommentEntity/deleteComment/"+commentId, commentId);

    }
}
