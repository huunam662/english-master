package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Mapper.CommentMapper;
import com.example.englishmaster_be.Mapper.PostMapper;
import com.example.englishmaster_be.Model.Request.Post.PostFilterRequest;
import com.example.englishmaster_be.Model.Request.Post.PostRequest;
import com.example.englishmaster_be.Model.Response.CommentResponse;
import com.example.englishmaster_be.Model.Response.PostResponse;
import com.example.englishmaster_be.Service.*;
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
    @MessageResponse("List PostEntity successfully")
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
    @MessageResponse("Create PostEntity successfully")
    public PostResponse createPost(@RequestBody PostRequest postRequest){

        PostEntity post = postService.savePost(postRequest);

        return PostMapper.INSTANCE.toPostResponse(post);
    }

    @PatchMapping(value = "/{postId:.+}/updatePost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Update PostEntity successfully")
    public PostResponse updatePost(@PathVariable UUID postId, @RequestBody PostRequest postRequest){

        postRequest.setPostId(postId);

        PostEntity post = postService.savePost(postRequest);

        return PostMapper.INSTANCE.toPostResponse(post);
    }

    @GetMapping(value = "/{postId:.+}/getAllCommentToPost")
    @MessageResponse("Show list CommentEntity successfully")
    public List<CommentResponse> getListCommentToPostId(@PathVariable UUID postId){

        List<CommentEntity> commentEntityList = postService.getListCommentToPostId(postId);

        return CommentMapper.INSTANCE.toCommentResponseList(commentEntityList);
    }

    @DeleteMapping(value = "/{postId:.+}/deletePost")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Delete PostEntity successfully")
    public void deletePost(@PathVariable UUID postId){

        postService.deletePost(postId);
    }
}
