package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Comment.CreateCommentDTO;
import com.example.englishmaster_be.DTO.Comment.UpdateCommentDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.Comment;
import com.example.englishmaster_be.Model.Post;
import com.example.englishmaster_be.Model.Response.CommentResponse;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.Topic;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.ICommentService;
import com.example.englishmaster_be.Service.IPostService;
import com.example.englishmaster_be.Service.ITopicService;
import com.example.englishmaster_be.Service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

    CommentRepository commentRepository;

    SimpMessagingTemplate messagingTemplate;

    IUserService userService;

    ITopicService topicService;

    IPostService postService;


    @Transactional
    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Transactional
    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public boolean checkCommentParent(Comment comment) {
        return commentRepository.existsByCommentParent(comment);
    }

    @Override
    public List<Comment> findAllByCommentParent(Comment commentParent) {
        if(!commentRepository.existsByCommentParent(commentParent))
            return null;

        return commentRepository.findAllByCommentParent(commentParent).stream().toList();
    }

    @Override
    public Comment findCommentToId(UUID commentID) {
        return commentRepository.findByCommentId(commentID)
                .orElseThrow(
                        () -> new IllegalArgumentException("Comment not found with ID: " + commentID)
                );
    }

    @Override
    public List<CommentResponse> getListCommentByCommentId(UUID commentId) {

        Comment comment = findCommentToId(commentId);

        List<Comment> commentList = findAllByCommentParent(comment);

        if(commentList == null) commentList = new ArrayList<>();

        return commentList.stream().map(
                commentItem -> new CommentResponse(
                        commentItem, checkCommentParent(commentItem)
                )
        ).toList();
    }

    @Transactional
    @Override
    public CommentResponse createCommentToTopic(UUID topicId, CreateCommentDTO createCommentDTO) {

        User user = userService.currentUser();

        Topic topic = topicService.findTopicById(topicId);

        Comment comment = Comment.builder()
                .userComment(user)
                .topic(topic)
                .content(createCommentDTO.getCommentContent())
                .build();

        comment = commentRepository.save(comment);

        CommentResponse commentResponse = new CommentResponse(comment, checkCommentParent(comment));

        messagingTemplate.convertAndSend("/Comment/Topic/"+topicId, commentResponse);

        return commentResponse;
    }

    @Transactional
    @Override
    public CommentResponse createCommentToPost(UUID postId, CreateCommentDTO createCommentDTO) {

        User user = userService.currentUser();

        Post post = postService.findPostById(postId);

        Comment comment = Comment.builder()
                .userComment(user)
                .post(post)
                .content(createCommentDTO.getCommentContent())
                .build();

        comment = commentRepository.save(comment);

        CommentResponse commentResponse = new CommentResponse(comment, checkCommentParent(comment));

        messagingTemplate.convertAndSend("/Comment/Post/"+postId, commentResponse);

        return commentResponse;
    }

    @Transactional
    @Override
    public CommentResponse createCommentToComment(UUID commentId, CreateCommentDTO createCommentDTO) {

        User user = userService.currentUser();

        Comment commentParent = findCommentToId(commentId);

        Comment comment = Comment.builder()
                .userComment(user)
                .commentParent(commentParent)
                .content(createCommentDTO.getCommentContent())
                .build();

        if(commentParent.getTopic() != null)
            comment.setTopic(commentParent.getTopic());

        if(commentParent.getPost() != null)
            comment.setPost(commentParent.getPost());

        comment = commentRepository.save(comment);

        CommentResponse commentResponse = new CommentResponse(comment, checkCommentParent(comment));

        messagingTemplate.convertAndSend("/Comment/commentParent/"+commentId, commentResponse);

        return commentResponse;
    }

    @Transactional
    @Override
    public CommentResponse updateComment(UpdateCommentDTO updateCommentDTO) {

        User user = userService.currentUser();

        Comment comment = findCommentToId(updateCommentDTO.getCommentId());

        if(!comment.getUserComment().getUserId().equals(user.getUserId()))
            throw new BadRequestException("Don't update Comment");

        comment.setContent(updateCommentDTO.getCommentContent());
        comment.setUpdateAt(LocalDateTime.now());

        comment  = commentRepository.save(comment);

        CommentResponse commentResponse = new CommentResponse(comment, checkCommentParent(comment));

        messagingTemplate.convertAndSend("/Comment/updateComment/"+commentResponse.getCommentId(), commentResponse);

        return commentResponse;
    }

    @Transactional
    @Override
    public void deleteComment(UUID commentId) {

        User user = userService.currentUser();

        Comment comment = findCommentToId(commentId);

        if(!comment.getUserComment().getUserId().equals(user.getUserId()))
            throw new BadRequestException("Don't update Comment");

        commentRepository.delete(comment);

        messagingTemplate.convertAndSend("/Comment/deleteComment/"+commentId, commentId);

    }
}
