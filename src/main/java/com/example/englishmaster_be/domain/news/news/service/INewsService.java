package com.example.englishmaster_be.domain.news.news.service;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.news.news.dto.req.UpdateNewsReq;
import com.example.englishmaster_be.domain.news.news.dto.req.CreateNewsReq;
import com.example.englishmaster_be.domain.news.news.dto.view.INewsPageView;
import com.example.englishmaster_be.domain.news.news.model.NewsEntity;
import com.example.englishmaster_be.common.dto.res.ResourceKeyRes;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface INewsService {

    NewsEntity getNewsById(UUID newsId);

    Page<INewsPageView> getPageNews(PageOptionsReq optionsReq);

    ResourceKeyRes createNews(CreateNewsReq newsRequest);

    ResourceKeyRes updateNews(UpdateNewsReq newsRequest);

    void enableNews(UUID newsId, boolean enable);

    void deleteNews(UUID newsId);

    List<NewsEntity> searchToTitle(String title);
}
