package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.DTO.Comment.*;
import com.example.englishmaster_be.Model.Response.*;
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
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

    ICommentService commentService;


    @GetMapping(value = "/{commentId:.+}/getAllComment")
    @MessageResponse("Show list Comment child successfully")
    public List<CommentResponse> getListCommentToCommentId(@PathVariable UUID commentId){

        return commentService.getListCommentByCommentId(commentId);
    }

    @PostMapping(value = "/{topicId:.+}/addCommentToTopic")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create Comment successfully")
    public CommentResponse createCommentToTopic(
            @PathVariable UUID topicId,
            @RequestBody SaveCommentDTO createCommentDTO
    ){

        return commentService.createCommentToTopic(topicId, createCommentDTO);
    }

    @PostMapping(value = "/{postId:.+}/addCommentToPost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create Comment successfully")
    public CommentResponse createCommentToPost(
            @PathVariable UUID postId,
            @RequestBody SaveCommentDTO createCommentDTO
    ){

        return commentService.createCommentToPost(postId, createCommentDTO);
    }


    @PostMapping(value = "/{commentId:.+}/addCommentToComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create Comment successfully")
    public CommentResponse createCommentToComment(
            @PathVariable UUID commentId,
            @RequestBody SaveCommentDTO createCommentDTO
    ){

        return commentService.createCommentToComment(commentId, createCommentDTO);
    }

    @PatchMapping(value = "/{commentId:.+}/updateComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Update Comment successfully")
    public CommentResponse updateComment(
            @PathVariable UUID commentId,
            @RequestBody UpdateCommentDTO updateCommentDTO
    ){

        updateCommentDTO.setCommentId(commentId);

        return commentService.updateComment(updateCommentDTO);
    }

    @DeleteMapping(value = "/{commentId:.+}/deleteComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Delete Comment successfully")
    public void deleteComment(@PathVariable UUID commentId){

        commentService.deleteComment(commentId);
    }
}
