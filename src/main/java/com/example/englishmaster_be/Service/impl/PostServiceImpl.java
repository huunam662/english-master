package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Post;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PostServiceImpl implements IPostService {
    
    @Autowired
    private PostRepository postRepository;
    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Override
    public Post findPostById(UUID postId) {
        return postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found with ID: " + postId));
    }
}
