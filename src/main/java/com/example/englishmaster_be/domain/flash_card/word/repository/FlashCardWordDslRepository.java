package com.example.englishmaster_be.domain.flash_card.word.repository;


import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.QFlashCardEntity;
import com.example.englishmaster_be.domain.flash_card.word.dto.view.IFlashCardWordPageView;
import com.example.englishmaster_be.domain.flash_card.word.model.QFlashCardWordEntity;
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
public class FlashCardWordDslRepository {

    @PersistenceContext
    private final EntityManager em;

    public FlashCardWordDslRepository(EntityManager em) {
        this.em = em;
    }

    public Page<IFlashCardWordPageView> findPageFlashCardWord(PageOptionsReq optionsReq){
        QFlashCardWordEntity flashCardWord = new QFlashCardWordEntity("flashCardWord");
        QFlashCardEntity flashCard = QFlashCardEntity.flashCardEntity;
        QUserEntity createBy = new QUserEntity("createBy");
        QUserEntity updateBy = new QUserEntity("updateBy");
        JPAQuery<IFlashCardWordPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                IFlashCardWordPageView.FlashCardWordPageView.class,
                flashCardWord.as(flashCardWord)
        ))
        .from(flashCardWord)
        .leftJoin(flashCardWord.flashCard, flashCard).fetchJoin()
        .leftJoin(flashCardWord.createBy, createBy).fetchJoin()
        .leftJoin(flashCardWord.updateBy, updateBy).fetchJoin();
        return DslUtil.fetchPage(em, query, Map.of(), optionsReq);
    }

    public Page<IFlashCardWordPageView> findPageFlashCardWordByFlashCard(FlashCardEntity flashCardEntity, PageOptionsReq optionsReq){
        QFlashCardWordEntity flashCardWord = new QFlashCardWordEntity("flashCardWord");
        QFlashCardEntity flashCard = QFlashCardEntity.flashCardEntity;
        QUserEntity createBy = new QUserEntity("createBy");
        QUserEntity updateBy = new QUserEntity("updateBy");
        JPAQuery<IFlashCardWordPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                IFlashCardWordPageView.FlashCardWordPageView.class,
                flashCardWord.as(flashCardWord)
        ))
        .from(flashCardWord)
        .leftJoin(flashCardWord.flashCard, flashCard).fetchJoin()
        .leftJoin(flashCardWord.createBy, createBy).fetchJoin()
        .leftJoin(flashCardWord.updateBy, updateBy).fetchJoin()
        .where(flashCard.eq(flashCardEntity));
        return DslUtil.fetchPage(em, query, optionsReq);
    }
}
