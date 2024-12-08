package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.PostCategory.SavePostCategoryDto;
import com.example.englishmaster_be.DTO.PostCategory.UpdatePostCategoryDto;

import java.util.UUID;

public interface IPostCategoryService {

    Object createPostCategory(SavePostCategoryDto dto);

    Object getAllPostCategory();

    Object getPostCategoryById(UUID id);

    Object updatePostCategory(UUID id, UpdatePostCategoryDto dto);

    Object getPostCategoryBySlug(String slug);

    Object deletePostCategory(UUID id);

}
