package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.DTO.Post.PostFilterRequest;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.DTO.Post.SavePostDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Service.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/post")
public class PostController {


    IPostService postService;



    @GetMapping(value = "/listPost")
    @MessageResponse("List Post successfully")
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
    @MessageResponse("Create Post successfully")
    public PostResponse createPost(@RequestBody SavePostDTO savePostDTO){

        return postService.savePost(null, savePostDTO);
    }

    @PatchMapping(value = "/{postId:.+}/updatePost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Update Post successfully")
    public PostResponse updatePost(@PathVariable UUID postId, @RequestBody SavePostDTO updatePostDTO){

        return postService.savePost(postId, updatePostDTO);
    }

    @GetMapping(value = "/{postId:.+}/getAllCommentToPost")
    @MessageResponse("Show list Comment successfully")
    public List<CommentResponse> getListCommentToPostId(@PathVariable UUID postId){

        return postService.getListCommentToPostId(postId);
    }

    @DeleteMapping(value = "/{postId:.+}/deletePost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Delete Post successfully")
    public void deletePost(@PathVariable UUID postId){

        postService.deletePost(postId);
    }
}
