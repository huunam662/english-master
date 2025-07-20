package com.example.englishmaster_be.domain.exam.pack.pack.repository;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.view.IPackPageView;
import com.example.englishmaster_be.domain.exam.pack.pack.model.QPackEntity;
import com.example.englishmaster_be.domain.exam.pack.type.model.QPackTypeEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.QTopicEntity;
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
public class PackDslRepository {

    @PersistenceContext
    private final EntityManager em;

    public PackDslRepository(EntityManager em) {
        this.em = em;
    }

    public Page<IPackPageView> findPagePack(PageOptionsReq optionsReq){
        QPackEntity pack = new QPackEntity("pack");
        QPackTypeEntity packType = QPackTypeEntity.packTypeEntity;
        QUserEntity userCreate = new QUserEntity("userCreate");
        QUserEntity userUpdate = new QUserEntity("userUpdate");
        QTopicEntity topic = QTopicEntity.topicEntity;
        String countTopics = "countTopics";
        Map<String, NumberExpression<?>> numberExpressionMap = Map.of(
                countTopics, topic.countDistinct()
        );
        JPAQuery<IPackPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                IPackPageView.PackPageView.class,
                pack.as(pack),
                numberExpressionMap.get(countTopics).as(countTopics)
        ))
        .from(pack)
        .leftJoin(pack.packType, packType).fetchJoin()
        .leftJoin(pack.userCreate, userCreate).fetchJoin()
        .leftJoin(pack.userUpdate, userUpdate).fetchJoin()
        .leftJoin(pack.topics, topic)
        .groupBy(pack, packType, userCreate, userUpdate);
        return DslUtil.fetchPage(em, query, numberExpressionMap, optionsReq);
    }
}
