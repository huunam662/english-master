package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.DTO.News.SaveNewsDTO;
import com.example.englishmaster_be.DTO.News.NewsFilterRequest;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.NewsResponse;

import java.util.List;
import java.util.UUID;

public interface INewsService {

    News findNewsById(UUID newsId);

    FilterResponse<?> listNewsOfAdmin(NewsFilterRequest filterRequest);

    List<NewsResponse> listNewsOfUser(NewsFilterRequest filterRequest);

    NewsResponse saveNews(SaveNewsDTO newsDTO);

    void enableNews(UUID newsId, boolean enable);

    void deleteNews(UUID newsId);

    List<NewsResponse> searchByTitle(String title);
}
