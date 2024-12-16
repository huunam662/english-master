package com.example.englishmaster_be.domain.posts.service;

import com.example.englishmaster_be.domain.posts.dto.request.PostRequest;
import com.example.englishmaster_be.domain.posts.dto.request.FilterPostRequest;
import com.example.englishmaster_be.domain.posts.dto.request.SelectPostRequest;
import com.example.englishmaster_be.domain.posts.dto.request.UpdatePostRequest;

import java.util.UUID;

public interface IPostsService {

    Object createPost(PostRequest dto);

    Object updatePost(UUID id, UpdatePostRequest dto);

    Object deletePost(UUID id);

    Object getAllPosts(SelectPostRequest dto);

    Object getPostByPostCategorySlug(String slug, FilterPostRequest dto);

    Object getPostBySlug(String slug);

    Object searchPost(FilterPostRequest dto);

    Object getById(UUID id);

}
