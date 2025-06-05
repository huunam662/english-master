package com.example.englishmaster_be.domain.comment.controller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.comment.dto.request.CreateCmToCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.request.UpdateCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.response.*;
import com.example.englishmaster_be.domain.comment.service.ICommentService;
import com.example.englishmaster_be.domain.comment.dto.request.CreateNewsCommentRequest;
import com.example.englishmaster_be.mapper.CommentMapper;
import com.example.englishmaster_be.model.comment.CommentEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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


    @GetMapping("/{commentId:.+}")
    @DefaultMessage("Load comment successful.")
    @Operation(
            summary = "Get single comment.",
            description = "Get single comment."
    )
    public CommentNewsResponse getComment(@PathVariable UUID commentId) {

        CommentEntity comment = commentService.getCommentInfoById(commentId);

        return CommentMapper.INSTANCE.toCommentNewsResponse(comment);
    }

    @GetMapping("/{newsId:.+}/news-comments")
    @DefaultMessage("load new comments successful.")
    @Operation(
            summary = "Get comments for news.",
            description = "Get comments for news."
    )
    public List<CommentNewsResponse> loadComments(
            @PathVariable("newsId") UUID newsId,
            @RequestParam("stepLoad") Integer stepLoad,
            @RequestParam("sizeLoad") Integer sizeLoad
    ){

        List<CommentEntity> comments = commentService.getNewsComments(newsId, stepLoad, sizeLoad);

        return CommentMapper.INSTANCE.toCommentNewsResponseList(comments);
    }

    @GetMapping("/{commentParentId:.+}/comments-child")
    @DefaultMessage("load comments child successful.")
    @Operation(
            summary = "Get comments child.",
            description = "Get comments child"
    )
    public List<CommentChildResponse> loadCommentsChild(
            @PathVariable("commentParentId") UUID commentParentId,
            @RequestParam("stepLoad") Integer stepLoad,
            @RequestParam("sizeLoad") Integer sizeLoad
    ){

        List<CommentEntity> comments = commentService.getCommentsChild(commentParentId, stepLoad, sizeLoad);

        return CommentMapper.INSTANCE.toCommentChildResponseList(comments);
    }

    @PostMapping("/to-news")
    @DefaultMessage("Comment for news successful.")
    @Operation(
            summary = "Comment for news.",
            description = "Comment for news."
    )
    public CommentNewsKeyResponse commentToNews(@RequestBody @Valid CreateNewsCommentRequest request){

        return commentService.commentToNews(request);
    }

    @PatchMapping("/edit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Edit comment successful.")
    @Operation(
            summary = "Edit comment.",
            description = "Edit comment."
    )
    public CommentKeyResponse editComment(@RequestBody @Valid UpdateCommentRequest request){

        return commentService.updateComment(request);
    }

    @DeleteMapping("/{commentId:.+}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Delete comment successful.")
    @Operation(
            summary = "Delete comment.",
            description = "Delete comment."
    )
    public void deleteComment(@PathVariable("commentId") UUID commentId){

        commentService.deleteComment(commentId);
    }

    @PostMapping("/to-comment")
    @DefaultMessage("Comment to comment successful.")
    @Operation(
            summary = "Comment to any comment.",
            description = "Comment to any comment."
    )
    public CmToCommentNewsKeyResponse commentToAnyComment(@RequestBody @Valid CreateCmToCommentRequest request){

        return commentService.commentToAnyComment(request);
    }

    @GetMapping("/{commentId:.+}/votes")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Votes to comment successful.")
    @Operation(
            summary = "Votes to comment.",
            description = "Votes to comment."
    )
    public CommentKeyResponse votesToComment(@PathVariable("commentId") UUID commentId){

        return commentService.votesToComment(commentId);
    }

    @GetMapping("/{commentId:.+}/un-votes")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("UnVotes to comment successful.")
    @Operation(
            summary = "UnVotes to comment.",
            description = "UnVotes to comment."
    )
    public CommentKeyResponse unVotesToComment(@PathVariable("commentId") UUID commentId){

        return commentService.unVotesToComment(commentId);
    }

}
