package com.example.englishmaster_be.domain.comment.controller;


import com.example.englishmaster_be.domain.comment.dto.request.CreateCmToCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.request.UpdateCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.response.*;
import com.example.englishmaster_be.domain.comment.service.ICommentService;
import com.example.englishmaster_be.domain.comment.dto.request.CreateNewsCommentRequest;
import com.example.englishmaster_be.domain.comment.mapper.CommentMapper;
import com.example.englishmaster_be.domain.comment.model.CommentEntity;
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
    @Operation(
            summary = "Get single comment.",
            description = "Get single comment."
    )
    public CommentNewsResponse getComment(@PathVariable UUID commentId) {

        CommentEntity comment = commentService.getCommentInfoById(commentId);

        return CommentMapper.INSTANCE.toCommentNewsResponse(comment);
    }

    @GetMapping("/{newsId:.+}/news-comments")
    @Operation(
            summary = "Get comments for news.",
            description = "Get comments for news."
    )
    public List<CommentNewsResponse> loadComments(
            @PathVariable("newsId") UUID newsId,
            @RequestParam("stepLoad") Integer stepLoad,
            @RequestParam("sizeLoad") Integer sizeLoad
    ){

        return commentService.getNewsComments(newsId, stepLoad, sizeLoad);
    }

    @GetMapping("/{commentParentId:.+}/comments-child")
    @Operation(
            summary = "Get comments child.",
            description = "Get comments child"
    )
    public List<CommentChildResponse> loadCommentsChild(
            @PathVariable("commentParentId") UUID commentParentId,
            @RequestParam("stepLoad") Integer stepLoad,
            @RequestParam("sizeLoad") Integer sizeLoad
    ){

        return commentService.getCommentsChild(commentParentId, stepLoad, sizeLoad);
    }

    @PostMapping("/to-news")
    @Operation(
            summary = "Comment for news.",
            description = "Comment for news."
    )
    public CommentNewsKeyResponse commentToNews(@RequestBody @Valid CreateNewsCommentRequest request){

        return commentService.commentToNews(request);
    }

    @PatchMapping("/edit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Edit comment.",
            description = "Edit comment."
    )
    public CommentKeyResponse editComment(@RequestBody @Valid UpdateCommentRequest request){

        return commentService.updateComment(request);
    }

    @DeleteMapping("/{commentId:.+}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Delete comment.",
            description = "Delete comment."
    )
    public void deleteComment(@PathVariable("commentId") UUID commentId){

        commentService.deleteComment(commentId);
    }

    @PostMapping("/to-comment")
    @Operation(
            summary = "Comment to any comment.",
            description = "Comment to any comment."
    )
    public CmToCommentNewsKeyResponse commentToAnyComment(@RequestBody @Valid CreateCmToCommentRequest request){

        return commentService.commentToAnyComment(request);
    }

    @GetMapping("/{commentId:.+}/votes")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Votes to comment.",
            description = "Votes to comment."
    )
    public CommentKeyResponse votesToComment(@PathVariable("commentId") UUID commentId){

        return commentService.votesToComment(commentId);
    }

    @GetMapping("/{commentId:.+}/un-votes")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "UnVotes to comment.",
            description = "UnVotes to comment."
    )
    public CommentKeyResponse unVotesToComment(@PathVariable("commentId") UUID commentId){

        return commentService.unVotesToComment(commentId);
    }

}
