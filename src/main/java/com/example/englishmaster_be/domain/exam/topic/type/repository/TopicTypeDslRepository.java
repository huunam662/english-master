package com.example.englishmaster_be.domain.exam.topic.type.repository;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.topic.topic.model.QTopicEntity;
import com.example.englishmaster_be.domain.exam.topic.type.dto.view.ITopicTypePageView;
import com.example.englishmaster_be.domain.exam.topic.type.model.QTopicTypeEntity;
import com.example.englishmaster_be.domain.user.user.model.QUserEntity;
import com.example.englishmaster_be.util.DslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class TopicTypeDslRepository {

    @PersistenceContext
    private final EntityManager em;

    public TopicTypeDslRepository(EntityManager em) {
        this.em = em;
    }

    public Page<ITopicTypePageView> getTopicTypePage(PageOptionsReq optionsReq){
        QTopicTypeEntity topicType = new QTopicTypeEntity("topicType");
        QTopicEntity topic = QTopicEntity.topicEntity;
        QUserEntity userCreate = new QUserEntity("userCreate");
        QUserEntity userUpdate = new QUserEntity("userUpdate");
        String countTopics = "countTopics";
        Map<String, NumberExpression<?>> numberExpressionMap = Map.of(
                countTopics, topic.countDistinct()
        );
        JPAQuery<ITopicTypePageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                ITopicTypePageView.TopicTypePageView.class,
                topicType.as(topicType),
                numberExpressionMap.get(countTopics).as(countTopics)
        ))
        .from(topicType)
        .leftJoin(topicType.userCreate, userCreate).fetchJoin()
        .leftJoin(topicType.userUpdate, userUpdate).fetchJoin()
        .leftJoin(topicType.topics, topic)
        .groupBy(topicType, userCreate, userUpdate);
        return DslUtil.fetchPage(em, query, numberExpressionMap, optionsReq);
    }
}
