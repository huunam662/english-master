package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.FlashCardWordResponse;
import com.example.englishmaster_be.Model.Response.NewsResponse;
import com.example.englishmaster_be.Model.Response.GeneralSearchAllResponse;
import com.example.englishmaster_be.Model.Response.TopicResponse;
import com.example.englishmaster_be.Repository.FlashCardWordRepository;
import com.example.englishmaster_be.Repository.NewsRepository;
import com.example.englishmaster_be.Repository.TopicRepository;
import com.example.englishmaster_be.Service.GeneralSearchService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeneralSearchServiceImpl implements GeneralSearchService {

    JPAQueryFactory queryFactory;


    @Override
    public GeneralSearchAllResponse searchAll(String keyword) {

        QTopic qTopic = QTopic.topic;

        List<Topic> topics = queryFactory.selectFrom(qTopic).where(qTopic.topicName.containsIgnoreCase(keyword)).fetch();

        GeneralSearchAllResponse searchAllResponse = new GeneralSearchAllResponse();

        searchAllResponse.setTopics(
                topics.stream().map(TopicResponse::new).toList()
        );

        QFlashCardWord qFlashCardWord = QFlashCardWord.flashCardWord;

        List<FlashCardWord> flashCardWords = queryFactory.selectFrom(qFlashCardWord).where(qFlashCardWord.word.containsIgnoreCase(keyword)).fetch();

        searchAllResponse.setFlashCardWords(
                flashCardWords.stream().map(FlashCardWordResponse::new).toList()
        );

        QNews qNews = QNews.news;

        List<News> newsList = queryFactory.selectFrom(qNews).where(qNews.title.containsIgnoreCase(keyword)).fetch();

        searchAllResponse.setNewsList(
                newsList.stream().map(NewsResponse::new).toList()
        );

        return searchAllResponse;
    }
}
