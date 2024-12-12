package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Mapper.FlashCardWordMapper;
import com.example.englishmaster_be.Mapper.NewsMapper;
import com.example.englishmaster_be.Mapper.TopicMapper;
import com.example.englishmaster_be.Model.Response.GeneralSearchAllResponse;
import com.example.englishmaster_be.Service.GeneralSearchService;
import com.example.englishmaster_be.entity.*;
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

        List<TopicEntity> topics = queryFactory.selectFrom(QTopicEntity.topicEntity).where(QTopicEntity.topicEntity.topicName.containsIgnoreCase(keyword)).fetch();

        List<FlashCardWordEntity> flashCardWords = queryFactory.selectFrom(QFlashCardWordEntity.flashCardWordEntity)
                .where(QFlashCardWordEntity.flashCardWordEntity.word.containsIgnoreCase(keyword)).fetch();

        List<NewsEntity> newsList = queryFactory.selectFrom(QNewsEntity.newsEntity).where(QNewsEntity.newsEntity.title.containsIgnoreCase(keyword)).fetch();

        return GeneralSearchAllResponse.builder()
                .topicList(TopicMapper.INSTANCE.toTopicResponseList(topics))
                .flashCardWordList(FlashCardWordMapper.INSTANCE.toFlashCardWordResponseList(flashCardWords))
                .newsList(NewsMapper.INSTANCE.toNewsResponseList(newsList))
                .build();

    }
}
