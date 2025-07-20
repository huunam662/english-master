package com.example.englishmaster_be.domain.flash_card.flash_card.repository;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.feedback.model.QFlashCardFeedbackEntity;
import com.example.englishmaster_be.domain.flash_card.flash_card.dto.view.IFlashCardPageView;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.QFlashCardEntity;
import com.example.englishmaster_be.domain.flash_card.word.model.QFlashCardWordEntity;
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
public class FlashCardDslRepository {

    @PersistenceContext
    private final EntityManager em;

    public FlashCardDslRepository(EntityManager em) {
        this.em = em;
    }

    public Page<IFlashCardPageView> findPageFlashCard(PageOptionsReq optionsReq){
        QFlashCardEntity flashCard = new QFlashCardEntity("flashCard");
        QUserEntity createBy = new QUserEntity("createBy");
        QUserEntity updateBy = new QUserEntity("updateBy");
        QFlashCardFeedbackEntity flashCardFeedback = QFlashCardFeedbackEntity.flashCardFeedbackEntity;
        QFlashCardWordEntity flashCardWord = QFlashCardWordEntity.flashCardWordEntity;
        String countFlashCardWords = "countFlashCardWords";
        String countFlashCardFeedbacks = "countFlashCardFeedbacks";
        Map<String, NumberExpression<?>> numberExpressionMap = Map.of(
                countFlashCardWords, flashCardWord.countDistinct(),
                countFlashCardFeedbacks, flashCardFeedback.countDistinct()
        );
        JPAQuery<IFlashCardPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                IFlashCardPageView.FlashCardPageView.class,
                flashCard.as(flashCard),
                numberExpressionMap.get(countFlashCardWords).as(countFlashCardWords),
                numberExpressionMap.get(countFlashCardFeedbacks).as(countFlashCardFeedbacks)
        )).from(flashCard)
                .leftJoin(flashCard.createBy, createBy).fetchJoin()
                .leftJoin(flashCard.updateBy, updateBy).fetchJoin()
                .leftJoin(flashCard.flashCardWords, flashCardWord)
                .leftJoin(flashCard.flashCardFeedbacks, flashCardFeedback)
                .groupBy(flashCard, createBy, updateBy);
        return DslUtil.fetchPage(em, query, numberExpressionMap, optionsReq);
    }

}
