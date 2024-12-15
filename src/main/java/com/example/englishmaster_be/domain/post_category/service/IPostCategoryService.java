package com.example.englishmaster_be.domain.post_category.service;

import com.example.englishmaster_be.domain.post_category.dto.request.PostCategoryRequest;

import java.util.UUID;

public interface IPostCategoryService {

    Object createPostCategory(PostCategoryRequest dto);

    Object getAllPostCategory();

    Object getPostCategoryById(UUID id);

    Object updatePostCategory(PostCategoryRequest dto);

    Object getPostCategoryBySlug(String slug);

    Object deletePostCategory(UUID id);

}
