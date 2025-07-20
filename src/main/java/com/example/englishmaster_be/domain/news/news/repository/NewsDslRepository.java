package com.example.englishmaster_be.domain.news.news.repository;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.news.comment.model.QCommentEntity;
import com.example.englishmaster_be.domain.news.news.dto.view.INewsPageView;
import com.example.englishmaster_be.domain.news.news.model.QNewsEntity;
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
public class NewsDslRepository {

    @PersistenceContext
    private final EntityManager em;

    public NewsDslRepository(EntityManager em) {
        this.em = em;
    }

    public Page<INewsPageView> findPageNews(PageOptionsReq optionsReq){
        QNewsEntity news = new QNewsEntity("news");
        QUserEntity userCreate = new QUserEntity("userCreate");
        QUserEntity userUpdate = new QUserEntity("userUpdate");
        QCommentEntity comment = QCommentEntity.commentEntity;
        String countComments = "countComments";
        Map<String, NumberExpression<?>> numberExpressionMap = Map.of(
                countComments, comment.countDistinct()
        );
        JPAQuery<INewsPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                INewsPageView.NewsPageView.class,
                news.as(news),
                numberExpressionMap.get(countComments).as(countComments)
        ))
        .from(news)
        .leftJoin(news.userCreate, userCreate).fetchJoin()
        .leftJoin(news.userUpdate, userUpdate).fetchJoin()
        .leftJoin(news.comments, comment)
        .groupBy(news, userCreate, userUpdate);
        return DslUtil.fetchPage(em, query, numberExpressionMap, optionsReq);
    }

}
