package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.model.request.Posts.PostRequest;
import com.example.englishmaster_be.model.request.Posts.FilterPostRequest;
import com.example.englishmaster_be.model.request.Posts.SelectPostRequest;
import com.example.englishmaster_be.model.request.Posts.UpdatePostRequest;
import com.example.englishmaster_be.service.IPostsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Posts")
public class PostsController {

    private final IPostsService postService;

    public PostsController(IPostsService postService) {
        this.postService = postService;
    }

    @PostMapping("/")
    public Object createPost(@RequestBody PostRequest dto) {
        return postService.createPost(dto);
    }

    @PatchMapping("/{id}")
    public Object updatePost(@PathVariable("id") UUID id, @RequestBody UpdatePostRequest dto) {
        return postService.updatePost(id, dto);
    }

    @DeleteMapping("/{id}")
    public Object deletePost(@PathVariable("id") UUID id) {
        return postService.deletePost(id);
    }

    @GetMapping("/id/{id}")
    public Object getPost(@PathVariable("id") UUID id) {
        return postService.getById(id);
    }


    @GetMapping("/")
    public Object getPosts(@ParameterObject SelectPostRequest dto) {
        return postService.getAllPosts(dto);
    }

    @GetMapping("/post-category/{slug}")
    public Object getPostsByCategory(@PathVariable("slug") String slug, @ParameterObject FilterPostRequest dto) {
        return postService.getPostByPostCategorySlug(slug, dto);
    }

    @GetMapping("/slug/{slug}")
    public Object getPostBySlug(@PathVariable("slug") String slug) {
        return postService.getPostBySlug(slug);
    }

    @GetMapping("/search")
    public Object searchPost(@ParameterObject FilterPostRequest dto) {
        return postService.searchPost(dto);
    }

}
