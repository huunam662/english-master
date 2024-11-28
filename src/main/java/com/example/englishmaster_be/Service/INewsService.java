package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.*;

import java.util.UUID;

public interface INewsService {
    void save(News news);

    void delete(News news);
    News findNewsById(UUID newsId);
}
