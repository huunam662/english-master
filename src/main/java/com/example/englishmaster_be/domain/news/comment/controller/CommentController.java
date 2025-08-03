package com.example.englishmaster_be.domain.news.comment.controller;


<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/news/comment/controller/CommentController.java
import com.example.englishmaster_be.domain.news.comment.dto.req.CreateCmToCommentReq;
import com.example.englishmaster_be.domain.news.comment.dto.req.UpdateCommentReq;
import com.example.englishmaster_be.domain.news.comment.dto.res.*;
import com.example.englishmaster_be.domain.news.comment.service.ICommentService;
import com.example.englishmaster_be.domain.news.comment.dto.req.CreateNewsCommentReq;
import com.example.englishmaster_be.domain.news.comment.mapper.CommentMapper;
import com.example.englishmaster_be.domain.news.comment.model.CommentEntity;
=======
import com.example.englishmaster_be.domain.comment.dto.request.CreateCmToCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.request.UpdateCommentRequest;
import com.example.englishmaster_be.domain.comment.dto.response.*;
import com.example.englishmaster_be.domain.comment.service.ICommentService;
import com.example.englishmaster_be.domain.comment.dto.request.CreateNewsCommentRequest;
import com.example.englishmaster_be.domain.comment.mapper.CommentMapper;
import com.example.englishmaster_be.domain.comment.model.CommentEntity;
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/comment/controller/CommentController.java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "Comment")
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final ICommentService commentService;

    public CommentController(ICommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{commentId:.+}")
    @Operation(
            summary = "Get single comment.",
            description = "Get single comment."
    )
    public CommentNewsRes getComment(@PathVariable UUID commentId) {

        CommentEntity comment = commentService.getCommentInfoById(commentId);

        return CommentMapper.INSTANCE.toCommentNewsResponse(comment);
    }

    @GetMapping("/{newsId:.+}/news-comments")
    @Operation(
            summary = "Get comments for news.",
            description = "Get comments for news."
    )
    public List<CommentNewsRes> loadComments(
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
    public List<CommentChildRes> loadCommentsChild(
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
    public CommentNewsKeyRes commentToNews(@RequestBody @Valid CreateNewsCommentReq request){

        return commentService.commentToNews(request);
    }

    @PatchMapping("/edit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Edit comment.",
            description = "Edit comment."
    )
    public CommentKeyRes editComment(@RequestBody @Valid UpdateCommentReq request){

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
    public CmToCommentNewsKeyRes commentToAnyComment(@RequestBody @Valid CreateCmToCommentReq request){

        return commentService.commentToAnyComment(request);
    }

    @GetMapping("/{commentId:.+}/votes")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Votes to comment.",
            description = "Votes to comment."
    )
    public CommentKeyRes votesToComment(@PathVariable("commentId") UUID commentId){

        return commentService.votesToComment(commentId);
    }

    @GetMapping("/{commentId:.+}/un-votes")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "UnVotes to comment.",
            description = "UnVotes to comment."
    )
    public CommentKeyRes unVotesToComment(@PathVariable("commentId") UUID commentId){

        return commentService.unVotesToComment(commentId);
    }

}
