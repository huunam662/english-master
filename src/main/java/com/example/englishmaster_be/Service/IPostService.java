package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.DTO.Post.PostFilterRequest;
import com.example.englishmaster_be.DTO.Post.SavePostDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.CommentResponse;
import com.example.englishmaster_be.Model.Response.PostResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface IPostService {

    Post findPostById(UUID postId);

    FilterResponse<?> getListPost(PostFilterRequest filterRequest);

    PostResponse savePost(UUID updatePostId, SavePostDTO savePostDTO);

    List<CommentResponse> getListCommentToPostId(UUID postId);

    void deletePost(UUID postId);

}
