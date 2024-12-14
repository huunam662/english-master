package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.common.dto.response.FilterResponse;

import com.example.englishmaster_be.mapper.CommentMapper;
import com.example.englishmaster_be.mapper.PostMapper;
import com.example.englishmaster_be.model.request.Post.PostFilterRequest;
import com.example.englishmaster_be.model.request.Post.PostRequest;
import com.example.englishmaster_be.model.response.CommentResponse;
import com.example.englishmaster_be.model.response.PostResponse;
import com.example.englishmaster_be.service.*;
import com.example.englishmaster_be.entity.CommentEntity;
import com.example.englishmaster_be.entity.PostEntity;
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
    @DefaultMessage("List PostEntity successfully")
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
    @DefaultMessage("Create PostEntity successfully")
    public PostResponse createPost(@RequestBody PostRequest postRequest){

        PostEntity post = postService.savePost(postRequest);

        return PostMapper.INSTANCE.toPostResponse(post);
    }

    @PatchMapping(value = "/{postId:.+}/updatePost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Update PostEntity successfully")
    public PostResponse updatePost(@PathVariable UUID postId, @RequestBody PostRequest postRequest){

        postRequest.setPostId(postId);

        PostEntity post = postService.savePost(postRequest);

        return PostMapper.INSTANCE.toPostResponse(post);
    }

    @GetMapping(value = "/{postId:.+}/getAllCommentToPost")
    @DefaultMessage("Show list CommentEntity successfully")
    public List<CommentResponse> getListCommentToPostId(@PathVariable UUID postId){

        List<CommentEntity> commentEntityList = postService.getListCommentToPostId(postId);

        return CommentMapper.INSTANCE.toCommentResponseList(commentEntityList);
    }

    @DeleteMapping(value = "/{postId:.+}/deletePost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Delete PostEntity successfully")
    public void deletePost(@PathVariable UUID postId){

        postService.deletePost(postId);
    }
}
