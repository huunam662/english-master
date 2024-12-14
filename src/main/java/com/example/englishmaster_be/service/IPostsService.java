package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.request.Posts.PostRequest;
import com.example.englishmaster_be.model.request.Posts.FilterPostRequest;
import com.example.englishmaster_be.model.request.Posts.SelectPostRequest;
import com.example.englishmaster_be.model.request.Posts.UpdatePostRequest;

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
