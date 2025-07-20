package com.example.englishmaster_be.domain.flash_card.feedback.repository;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.view.IFlashCardFeedbackPageView;
import com.example.englishmaster_be.domain.flash_card.feedback.model.QFlashCardFeedbackEntity;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.QFlashCardEntity;
import com.example.englishmaster_be.domain.user.user.model.QUserEntity;
import com.example.englishmaster_be.util.DslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class FlashCardFeedbackDslRepository {

    @PersistenceContext
    private final EntityManager em;

    public FlashCardFeedbackDslRepository(EntityManager em) {
        this.em = em;
    }

    public Page<IFlashCardFeedbackPageView> findPageFlashCardFeedback(PageOptionsReq optionsReq){
        QFlashCardFeedbackEntity flashCardFeedback = new QFlashCardFeedbackEntity("flashCardFeedback");
        QFlashCardEntity flashCard = QFlashCardEntity.flashCardEntity;
        QUserEntity userFeedback = QUserEntity.userEntity;
        JPAQuery<IFlashCardFeedbackPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                IFlashCardFeedbackPageView.FlashCardFeedbackPageView.class,
                flashCardFeedback.as(flashCardFeedback)
        ))
        .from(flashCardFeedback)
        .leftJoin(flashCardFeedback.flashCard, flashCard).fetchJoin()
        .leftJoin(flashCardFeedback.userFeedback, userFeedback).fetchJoin();
        return DslUtil.fetchPage(em, query, Map.of(), optionsReq);
    }
    
    public Page<IFlashCardFeedbackPageView> findPageFlashCardFeedbackByFlashCard(FlashCardEntity flashCardEntity, PageOptionsReq optionsReq){
        QFlashCardFeedbackEntity flashCardFeedback = new QFlashCardFeedbackEntity("flashCardFeedback");
        QFlashCardEntity flashCard = QFlashCardEntity.flashCardEntity;
        QUserEntity userFeedback = QUserEntity.userEntity;
        JPAQuery<IFlashCardFeedbackPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                IFlashCardFeedbackPageView.FlashCardFeedbackPageView.class,
                flashCardFeedback.as(flashCardFeedback)
        ))
        .from(flashCardFeedback)
        .leftJoin(flashCardFeedback.flashCard, flashCard).fetchJoin()
        .leftJoin(flashCardFeedback.userFeedback, userFeedback).fetchJoin()
        .where(flashCard.eq(flashCardEntity));
        return DslUtil.fetchPage(em, query, Map.of(), optionsReq);
    }
}
