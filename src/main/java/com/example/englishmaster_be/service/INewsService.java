package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.*;

import java.util.List;
import java.util.UUID;

public interface INewsService {
    void save(News news);

    void delete(News news);
    News findNewsById(UUID newsId);
}
