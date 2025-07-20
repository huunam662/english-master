package com.example.englishmaster_be.domain.exam.topic.topic.repository;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.pack.pack.model.QPackEntity;
import com.example.englishmaster_be.domain.exam.pack.type.model.QPackTypeEntity;
import com.example.englishmaster_be.domain.exam.part.model.QPartEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.view.ITopicPageView;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.QTopicEntity;
import com.example.englishmaster_be.domain.exam.topic.type.model.QTopicTypeEntity;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.QMockTestEntity;
import com.example.englishmaster_be.domain.user.user.model.QUserEntity;
import com.example.englishmaster_be.util.DslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Slf4j(topic = "TOPIC-SPEC-REPOSITORY")
@Repository
public class TopicDslRepository {

    @PersistenceContext
    private final EntityManager em;

    public TopicDslRepository(EntityManager em) {
        this.em = em;
    }

    public Page<ITopicPageView> findPageTopic(PageOptionsReq optionsReq){
        QTopicEntity topic = new QTopicEntity("topic");
        QTopicTypeEntity topicType = QTopicTypeEntity.topicTypeEntity;
        QPackEntity pack = QPackEntity.packEntity;
        QPackTypeEntity packType = QPackTypeEntity.packTypeEntity;
        QUserEntity userCreate = new QUserEntity("userCreate");
        QUserEntity userUpdate = new QUserEntity("userUpdate");
        QPartEntity part = QPartEntity.partEntity;
        QMockTestEntity mockTest = QMockTestEntity.mockTestEntity;
        String countMockTests = "countMockTests";
        String countParts = "countParts";
        Map<String, NumberExpression<?>> numberExpressionMap = Map.of(
                countMockTests, mockTest.countDistinct(),
                countParts, part.countDistinct()
        );
        JPAQuery<ITopicPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                ITopicPageView.TopicPageView.class,
                topic.as(topic),
                numberExpressionMap.get(countMockTests).as(countMockTests),
                numberExpressionMap.get(countParts).as(countParts)
        ))
        .from(topic)
        .leftJoin(topic.pack, pack).fetchJoin()
        .leftJoin(topic.topicType, topicType).fetchJoin()
        .leftJoin(topic.pack.packType, packType).fetchJoin()
        .leftJoin(topic.userCreate, userCreate).fetchJoin()
        .leftJoin(topic.userUpdate, userUpdate).fetchJoin()
        .leftJoin(topic.parts, part)
        .leftJoin(topic.mockTests, mockTest)
        .groupBy(topic, topicType, pack, packType, userCreate, userUpdate);
        return DslUtil.fetchPage(em, query, numberExpressionMap, optionsReq);
    }

    public Page<ITopicPageView> findPageTopicByPack(PackEntity packEntity, PageOptionsReq optionsReq){
        QTopicEntity topic = new QTopicEntity("topic");
        QTopicTypeEntity topicType = QTopicTypeEntity.topicTypeEntity;
        QPackEntity pack = QPackEntity.packEntity;
        QPackTypeEntity packType = QPackTypeEntity.packTypeEntity;
        QUserEntity userCreate = new QUserEntity("userCreate");
        QUserEntity userUpdate = new QUserEntity("userUpdate");
        QPartEntity part = QPartEntity.partEntity;
        QMockTestEntity mockTest = QMockTestEntity.mockTestEntity;
        String countMockTests = "countMockTests";
        String countParts = "countParts";
        Map<String, NumberExpression<?>> numberExpressionMap = Map.of(
                countMockTests, mockTest.countDistinct(),
                countParts, part.countDistinct()
        );
        JPAQuery<ITopicPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                        ITopicPageView.TopicPageView.class,
                        topic.as(topic),
                        numberExpressionMap.get(countMockTests).as(countMockTests),
                        numberExpressionMap.get(countParts).as(countParts)
                ))
                .from(topic)
                .leftJoin(topic.pack, pack).fetchJoin()
                .leftJoin(topic.topicType, topicType).fetchJoin()
                .leftJoin(topic.pack.packType, packType).fetchJoin()
                .leftJoin(topic.userCreate, userCreate).fetchJoin()
                .leftJoin(topic.userUpdate, userUpdate).fetchJoin()
                .leftJoin(topic.parts, part)
                .leftJoin(topic.mockTests, mockTest)
                .where(pack.eq(packEntity))
                .groupBy(topic, topicType, pack, packType, userCreate, userUpdate);
        return DslUtil.fetchPage(em, query, numberExpressionMap, optionsReq);
    }

}
