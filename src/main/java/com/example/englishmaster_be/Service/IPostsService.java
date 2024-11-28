package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Posts.CreatePostDto;
import com.example.englishmaster_be.DTO.Posts.FilterPostDto;
import com.example.englishmaster_be.DTO.Posts.SelectPostDto;
import com.example.englishmaster_be.DTO.Posts.UpdatePostDto;

import java.util.UUID;

public interface IPostsService {

    Object createPost(CreatePostDto dto);

    Object updatePost(UUID id, UpdatePostDto dto);

    Object deletePost(UUID id);

    Object getAllPosts(SelectPostDto dto);

    Object getPostByPostCategorySlug(String slug, FilterPostDto dto);

    Object getPostBySlug(String slug);

    Object searchPost(FilterPostDto dto);

    Object getById(UUID id);

}
