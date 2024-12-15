package com.example.englishmaster_be.domain.post.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.post.dto.request.PostFilterRequest;
import com.example.englishmaster_be.domain.post.dto.request.PostRequest;
import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.post.PostEntity;

import java.util.List;
import java.util.UUID;

public interface IPostService {

    PostEntity getPostById(UUID postId);

    FilterResponse<?> getListPost(PostFilterRequest filterRequest);

    PostEntity savePost(PostRequest postRequest);

    List<CommentEntity> getListCommentToPostId(UUID postId);

    void deletePost(UUID postId);

}
