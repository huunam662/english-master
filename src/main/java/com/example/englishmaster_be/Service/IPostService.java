package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Model.Request.Post.PostFilterRequest;
import com.example.englishmaster_be.Model.Request.Post.PostRequest;
import com.example.englishmaster_be.Model.Response.CommentResponse;
import com.example.englishmaster_be.Model.Response.PostResponse;
import com.example.englishmaster_be.entity.CommentEntity;
import com.example.englishmaster_be.entity.PostEntity;

import java.util.List;
import java.util.UUID;

public interface IPostService {

    PostEntity getPostById(UUID postId);

    FilterResponse<?> getListPost(PostFilterRequest filterRequest);

    PostEntity savePost(PostRequest postRequest);

    List<CommentEntity> getListCommentToPostId(UUID postId);

    void deletePost(UUID postId);

}
