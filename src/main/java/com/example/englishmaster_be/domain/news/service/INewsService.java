package com.example.englishmaster_be.domain.news.service;

import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.news.dto.request.NewsRequest;
import com.example.englishmaster_be.domain.news.dto.request.NewsFilterRequest;
import com.example.englishmaster_be.model.news.NewsEntity;

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
