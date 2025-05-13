package com.example.englishmaster_be.domain.news.service;

import com.example.englishmaster_be.domain.news.dto.request.UpdateNewsRequest;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.news.dto.request.CreateNewsRequest;
import com.example.englishmaster_be.domain.news.dto.request.NewsFilterRequest;
import com.example.englishmaster_be.model.news.NewsEntity;
import com.example.englishmaster_be.shared.dto.response.ResourceKeyResponse;

import java.util.List;
import java.util.UUID;

public interface INewsService {

    NewsEntity getNewsById(UUID newsId);

    FilterResponse<?> listNewsOfAdmin(NewsFilterRequest filterRequest);

    List<NewsEntity> listNewsOfUser(NewsFilterRequest filterRequest);

    ResourceKeyResponse createNews(CreateNewsRequest newsRequest);

    ResourceKeyResponse updateNews(UpdateNewsRequest newsRequest);

    void enableNews(UUID newsId, boolean enable);

    void deleteNews(UUID newsId);

    List<NewsEntity> searchByTitle(String title);
}
