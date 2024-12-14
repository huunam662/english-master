package com.example.englishmaster_be.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.model.request.News.NewsRequest;
import com.example.englishmaster_be.model.request.News.NewsFilterRequest;
import com.example.englishmaster_be.entity.NewsEntity;

import java.util.List;
import java.util.UUID;

public interface INewsService {

    NewsEntity findNewsById(UUID newsId);

    FilterResponse<?> listNewsOfAdmin(NewsFilterRequest filterRequest);

    List<NewsEntity> listNewsOfUser(NewsFilterRequest filterRequest);

    NewsEntity saveNews(NewsRequest newsRequest);

    void enableNews(UUID newsId, boolean enable);

    void deleteNews(UUID newsId);

    List<NewsEntity> searchByTitle(String title);
}
