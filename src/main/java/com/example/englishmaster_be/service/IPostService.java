package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.*;

import java.util.UUID;

public interface IPostService {
    void save(Post post);
    void delete(Post post);

    Post findPostById(UUID postId);
}
