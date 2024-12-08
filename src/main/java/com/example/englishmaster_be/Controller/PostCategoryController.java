package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.PostCategory.SavePostCategoryDto;
import com.example.englishmaster_be.DTO.PostCategory.UpdatePostCategoryDto;
import com.example.englishmaster_be.Service.IPostCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/post-category", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Post Category")
public class PostCategoryController {

    private final IPostCategoryService postCategoryService;

    public PostCategoryController(IPostCategoryService postCategoryService) {
        this.postCategoryService = postCategoryService;
    }


    @PostMapping("/")
    public Object createPostCategory(@RequestBody SavePostCategoryDto dto) {
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
    public Object updatePostCategory(@PathVariable("id") UUID id,@RequestBody UpdatePostCategoryDto dto) {
        return postCategoryService.updatePostCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    public Object deletePostCategory(@PathVariable("id") UUID id) {
        return postCategoryService.deletePostCategory(id);
    }

}
