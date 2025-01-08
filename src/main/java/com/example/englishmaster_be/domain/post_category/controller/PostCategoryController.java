package com.example.englishmaster_be.domain.post_category.controller;

import com.example.englishmaster_be.domain.post_category.dto.request.PostCategoryRequest;
import com.example.englishmaster_be.domain.post_category.service.IPostCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/post-category")
@Tag(name = "Post Category")
public class PostCategoryController {

    private final IPostCategoryService postCategoryService;

    public PostCategoryController(IPostCategoryService postCategoryService) {
        this.postCategoryService = postCategoryService;
    }


    @PostMapping("/")
    public Object createPostCategory(@RequestBody PostCategoryRequest dto) {
        return postCategoryService.createPostCategory(dto);
    }

    @GetMapping("/")
    public Object getAllPostCategory() {
        return postCategoryService.getAllPostCategory();
    }

    @GetMapping("/{id}")
    public Object getPostCategoryById(@PathVariable("id") UUID id) {
        return postCategoryService.getPostCategoryById(id);
    }

    @GetMapping("/slug/{slug}")
    public Object getPostCategoryBySlug(@PathVariable("slug") String slug) {
        return postCategoryService.getPostCategoryBySlug(slug);
    }

    @PatchMapping("/{id}")
    public Object updatePostCategory(@PathVariable("id") UUID id, @RequestBody PostCategoryRequest dto) {

        dto.setId(id);

        return postCategoryService.updatePostCategory(dto);
    }

    @DeleteMapping("/{id}")
    public Object deletePostCategory(@PathVariable("id") UUID id) {
        return postCategoryService.deletePostCategory(id);
    }

}
