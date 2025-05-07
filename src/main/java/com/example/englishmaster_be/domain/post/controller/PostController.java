package com.example.englishmaster_be.domain.post.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;

import com.example.englishmaster_be.domain.post.service.IPostService;
import com.example.englishmaster_be.mapper.CommentMapper;
import com.example.englishmaster_be.mapper.PostMapper;
import com.example.englishmaster_be.domain.post.dto.request.PostFilterRequest;
import com.example.englishmaster_be.domain.post.dto.request.PostRequest;
import com.example.englishmaster_be.domain.comment.dto.response.CommentResponse;
import com.example.englishmaster_be.domain.post.dto.response.PostResponse;
import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.post.PostEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Post")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {


    IPostService postService;



    @GetMapping(value = "/listPost")
    @DefaultMessage("List post successfully")
    public FilterResponse<?> listPost(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection
    ){

        PostFilterRequest filterRequest = PostFilterRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        return postService.getListPost(filterRequest);
    }

    @PostMapping(value = "/createPost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Create post successfully")
    public PostResponse createPost(@RequestBody PostRequest postRequest){

        PostEntity post = postService.savePost(postRequest);

        return PostMapper.INSTANCE.toPostResponse(post);
    }

    @PatchMapping(value = "/{postId:.+}/updatePost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Update post successfully")
    public PostResponse updatePost(@PathVariable UUID postId, @RequestBody PostRequest postRequest){

        postRequest.setPostId(postId);

        PostEntity post = postService.savePost(postRequest);

        return PostMapper.INSTANCE.toPostResponse(post);
    }

    @GetMapping(value = "/{postId:.+}/getAllCommentToPost")
    @DefaultMessage("Show list comment successfully")
    public List<CommentResponse> getListCommentToPostId(@PathVariable UUID postId){

        List<CommentEntity> commentEntityList = postService.getListCommentToPostId(postId);

        return CommentMapper.INSTANCE.toCommentResponseList(commentEntityList);
    }

    @DeleteMapping(value = "/{postId:.+}/deletePost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Delete post successfully")
    public void deletePost(@PathVariable UUID postId){

        postService.deletePost(postId);
    }
}
