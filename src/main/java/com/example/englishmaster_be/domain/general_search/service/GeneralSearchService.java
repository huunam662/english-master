package com.example.englishmaster_be.domain.general_search.service;

import com.example.englishmaster_be.domain.flash_card_word.mapper.FlashCardWordMapper;
import com.example.englishmaster_be.domain.news.mapper.NewsMapper;
import com.example.englishmaster_be.domain.topic.mapper.TopicMapper;
import com.example.englishmaster_be.domain.general_search.dto.response.GeneralSearchAllResponse;
import com.example.englishmaster_be.domain.flash_card_word.model.FlashCardWordEntity;
import com.example.englishmaster_be.model.flash_card_word.QFlashCardWordEntity;
import com.example.englishmaster_be.domain.news.model.NewsEntity;
import com.example.englishmaster_be.model.news.QNewsEntity;
import com.example.englishmaster_be.model.topic.QTopicEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeneralSearchService implements IGeneralSearchService {

    JPAQueryFactory queryFactory;


    @Override
    public GeneralSearchAllResponse searchAll(String keyword) {

        int offset = 0;

        int limit = 5;

        String likeExpression = "%" + keyword.trim().replaceAll("\\s+", "%") + "%";

        List<TopicEntity> topics = queryFactory.selectFrom(QTopicEntity.topicEntity)
                .where(QTopicEntity.topicEntity.topicName.like(likeExpression))
                .offset(offset)
                .limit(limit)
                .fetch();

        List<FlashCardWordEntity> flashCardWords = queryFactory.selectFrom(QFlashCardWordEntity.flashCardWordEntity)
                .where(QFlashCardWordEntity.flashCardWordEntity.word.like(likeExpression))
                .offset(offset)
                .limit(limit)
                .fetch();

        List<NewsEntity> newsList = queryFactory.selectFrom(QNewsEntity.newsEntity)
                .where(QNewsEntity.newsEntity.title.like(likeExpression))
                .offset(offset)
                .limit(limit)
                .fetch();

        return GeneralSearchAllResponse.builder()
                .topicList(TopicMapper.INSTANCE.toTopicResponseList(topics))
                .flashCardWordList(FlashCardWordMapper.INSTANCE.toFlashCardWordResponseList(flashCardWords))
                .newsList(NewsMapper.INSTANCE.toNewsResponseList(newsList))
                .build();

    }
}
