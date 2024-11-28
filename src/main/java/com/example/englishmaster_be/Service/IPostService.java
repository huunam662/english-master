package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.*;

import java.util.UUID;

public interface IPostService {
    void save(Post post);
    void delete(Post post);

    Post findPostById(UUID postId);
}
