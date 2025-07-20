package com.example.englishmaster_be.domain.news.news.dto.view;

import com.example.englishmaster_be.domain.news.news.model.NewsEntity;
import lombok.Data;

public interface INewsPageView {
    NewsEntity getNews();
    Long getCountComments();

    @Data
    class NewsPageView implements INewsPageView{
        private NewsEntity news;
        private Long countComments;
    }
}
