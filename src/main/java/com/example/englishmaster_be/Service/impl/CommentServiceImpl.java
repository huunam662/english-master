package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Request.Comment.CommentRequest;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Mapper.CommentMapper;
import com.example.englishmaster_be.entity.CommentEntity;
import com.example.englishmaster_be.entity.PostEntity;
import com.example.englishmaster_be.entity.TopicEntity;
import com.example.englishmaster_be.entity.UserEntity;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.ICommentService;
import com.example.englishmaster_be.Service.IPostService;
import com.example.englishmaster_be.Service.ITopicService;
import com.example.englishmaster_be.Service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements ICommentService {

    SimpMessagingTemplate messagingTemplate;

    CommentRepository commentRepository;

    IUserService userService;

    ITopicService topicService;

    IPostService postService;



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
                        () -> new IllegalArgumentException("CommentEntity not found with ID: " + commentID)
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

        messagingTemplate.convertAndSend("/CommentEntity/TopicEntity/" + topicId, CommentMapper.INSTANCE.toCommentResponse(comment));

        return comment;
    }

    @Transactional
    @Override
    public CommentEntity saveCommentToPost(UUID postId, CommentRequest commentRequest) {

        UserEntity user = userService.currentUser();

        PostEntity post = postService.getPostById(postId);

        CommentEntity comment = CommentEntity.builder()
                .userComment(user)
                .post(post)
                .content(commentRequest.getCommentContent())
                .build();

        comment = commentRepository.save(comment);

        messagingTemplate.convertAndSend("/CommentEntity/PostEntity/" + postId, CommentMapper.INSTANCE.toCommentResponse(comment));

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

        if(commentParent.getPost() != null)
            comment.setPost(commentParent.getPost());

        comment = commentRepository.save(comment);

        messagingTemplate.convertAndSend("/CommentEntity/commentParent/" + commentId, CommentMapper.INSTANCE.toCommentResponse(comment));

        return comment;
    }

    @Transactional
    @Override
    public CommentEntity saveComment(UUID updateCommentId, CommentRequest commentRequest) {

        UserEntity user = userService.currentUser();

        CommentEntity comment = getCommentById(updateCommentId);

        if(!comment.getUserComment().getUserId().equals(user.getUserId()))
            throw new BadRequestException("Don't update CommentEntity");

        comment.setContent(commentRequest.getCommentContent());
        comment.setUpdateAt(LocalDateTime.now());

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
            throw new BadRequestException("Don't update CommentEntity");

        commentRepository.delete(comment);

        messagingTemplate.convertAndSend("/CommentEntity/deleteComment/"+commentId, commentId);

    }
}
