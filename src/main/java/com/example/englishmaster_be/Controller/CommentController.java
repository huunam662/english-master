package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Request.Comment.CommentRequest;
import com.example.englishmaster_be.Mapper.CommentMapper;
import com.example.englishmaster_be.Model.Response.CommentResponse;
import com.example.englishmaster_be.entity.CommentEntity;
import com.example.englishmaster_be.Service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Comment")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

    ICommentService commentService;


    @GetMapping(value = "/{commentId:.+}/getAllComment")
    @MessageResponse("Show list CommentEntity child successfully")
    public List<CommentResponse> getListCommentToCommentId(@PathVariable UUID commentId){

        List<CommentEntity> commentList = commentService.getListCommentByCommentId(commentId);

        return CommentMapper.INSTANCE.toCommentResponseList(commentList);
    }

    @PostMapping(value = "/{topicId:.+}/addCommentToTopic")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create CommentEntity successfully")
    public CommentResponse createCommentToTopic(
            @PathVariable UUID topicId,
            @RequestBody CommentRequest commentRequest
    ){

        CommentEntity comment = commentService.saveCommentToTopic(topicId, commentRequest);

        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    @PostMapping(value = "/{postId:.+}/addCommentToPost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create CommentEntity successfully")
    public CommentResponse createCommentToPost(
            @PathVariable UUID postId,
            @RequestBody CommentRequest commentRequest
    ){

        CommentEntity comment = commentService.saveCommentToPost(postId, commentRequest);

        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }


    @PostMapping(value = "/{commentId:.+}/addCommentToComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create CommentEntity successfully")
    public CommentResponse createCommentToComment(
            @PathVariable UUID commentId,
            @RequestBody CommentRequest commentRequest
    ){

        CommentEntity comment = commentService.saveCommentToComment(commentId, commentRequest);

        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    @PatchMapping(value = "/{commentId:.+}/updateComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Update CommentEntity successfully")
    public CommentResponse updateComment(
            @PathVariable UUID commentId,
            @RequestBody CommentRequest commentRequest
    ){

        CommentEntity comment = commentService.saveComment(commentId, commentRequest);

        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    @DeleteMapping(value = "/{commentId:.+}/deleteComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Delete CommentEntity successfully")
    public void deleteComment(@PathVariable UUID commentId){

        commentService.deleteComment(commentId);
    }
}
