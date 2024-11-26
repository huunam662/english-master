package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.FlashCardWordResponse;
import com.example.englishmaster_be.Model.Response.NewsResponse;
import com.example.englishmaster_be.Model.Response.TopicResponse;
import com.example.englishmaster_be.Repository.FlashCardWordRepository;
import com.example.englishmaster_be.Repository.NewsRepository;
import com.example.englishmaster_be.Repository.TopicRepository;
import com.example.englishmaster_be.Service.GeneralSearchService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeneralSearchServiceImpl implements GeneralSearchService {

    public final TopicRepository topicRepository;
    public final FlashCardWordRepository flashCardWordRepository;
    public final NewsRepository newsRepository;
    public final JPAQueryFactory queryFactory;

    public GeneralSearchServiceImpl(TopicRepository topicRepository, FlashCardWordRepository flashCardWordRepository, NewsRepository newsRepository, JPAQueryFactory queryFactory) {
        this.topicRepository = topicRepository;
        this.flashCardWordRepository = flashCardWordRepository;
        this.newsRepository = newsRepository;
        this.queryFactory = queryFactory;
    }

    @Override
    public Map<String, List<Object>> searchAll(String keyword) {
        Map<String, List<Object>> result = new HashMap<>();

        QTopic qTopic = QTopic.topic;
        List<Topic> topics = queryFactory.selectFrom(qTopic).where(qTopic.topicName.containsIgnoreCase(keyword)).fetch();
        List<TopicResponse> topicResponseList = new ArrayList<>();
        for (Topic topic : topics) {
            TopicResponse topicResponse = new TopicResponse(topic);
            topicResponseList.add(topicResponse);
        }
        result.put("topics", (List<Object>) (Object) topicResponseList);

        QFlashCardWord qFlashCardWord = QFlashCardWord.flashCardWord;
        List<FlashCardWord> flashCardWords = queryFactory.selectFrom(qFlashCardWord).where(qFlashCardWord.word.containsIgnoreCase(keyword)).fetch();
        List<FlashCardWordResponse> flashCardWordResponseList = new ArrayList<>();
        for (FlashCardWord flashCardWord : flashCardWords) {
            FlashCardWordResponse flashCardWordResponse = new FlashCardWordResponse(flashCardWord);
            flashCardWordResponseList.add(flashCardWordResponse);
        }
        result.put("flashCardWords", (List<Object>) (Object) flashCardWordResponseList);

        QNews qNews = QNews.news;
        List<News> newsList = queryFactory.selectFrom(qNews).where(qNews.title.containsIgnoreCase(keyword)).fetch();
        List<NewsResponse> newsResponseList = new ArrayList<>();
        for (News news : newsList) {
            NewsResponse newsResponse = new NewsResponse(news);
            newsResponseList.add(newsResponse);
        }
        result.put("newsList", (List<Object>) (Object) newsResponseList);
        return result;
    }
}
