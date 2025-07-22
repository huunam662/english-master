package com.example.englishmaster_be.domain.user.user.repository;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.QFlashCardEntity;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.QMockTestEntity;
import com.example.englishmaster_be.domain.news.comment.model.QCommentEntity;
import com.example.englishmaster_be.domain.news.news.model.QNewsEntity;
import com.example.englishmaster_be.domain.user.user.dto.view.IUserPageView;
import com.example.englishmaster_be.domain.user.user.model.QRoleEntity;
import com.example.englishmaster_be.domain.user.user.model.QUserEntity;
import com.example.englishmaster_be.util.DslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Slf4j(topic = "USER-SPEC-REPOSITORY")
@Repository
public class UserDslRepository {

    @PersistenceContext
    private final EntityManager em;

    public UserDslRepository(EntityManager em) {
        this.em = em;
    }

    public Page<IUserPageView> findPageUser(PageOptionsReq optionsReq){
        final QUserEntity user = new QUserEntity("user");
        final QFlashCardEntity flashCard = QFlashCardEntity.flashCardEntity;
        final QMockTestEntity mockTest = QMockTestEntity.mockTestEntity;
        final QCommentEntity comment = QCommentEntity.commentEntity;
        final QNewsEntity news = QNewsEntity.newsEntity;
        final QRoleEntity role = QRoleEntity.roleEntity;
        final String countFlashCards = "countFlashCards";
        final String countMockTests = "countMockTests";
        final String countNews = "countNews";
        final String countComments = "countComments";
        final Map<String, NumberExpression<?>> numberExpressionMap = Map.of(
                countFlashCards, flashCard.countDistinct(),
                countMockTests, mockTest.countDistinct(),
                countNews, news.countDistinct(),
                countComments, comment.countDistinct()
        );
        JPAQuery<IUserPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                IUserPageView.UserPageView.class,
                user.as(user),
                numberExpressionMap.get(countFlashCards).as(countFlashCards),
                numberExpressionMap.get(countMockTests).as(countMockTests),
                numberExpressionMap.get(countComments).as(countComments),
                numberExpressionMap.get(countNews).as(countNews)
        ))
        .from(user)
        .leftJoin(user.role, role).fetchJoin()
        .leftJoin(user.flashCards, flashCard)
        .leftJoin(user.mockTests, mockTest)
        .leftJoin(user.news, news)
        .leftJoin(user.comments, comment)
        .groupBy(user, role);
        return DslUtil.fetchPage(em, query, numberExpressionMap, optionsReq);
    }

}
