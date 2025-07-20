package com.example.englishmaster_be.domain.feedback.repository;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.feedback.dto.view.IFeedbackPageView;
import com.example.englishmaster_be.domain.feedback.model.QFeedbackEntity;
import com.example.englishmaster_be.util.DslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class FeedbackDslRepository {

    @PersistenceContext
    private final EntityManager em;

    public FeedbackDslRepository(EntityManager em) {
        this.em = em;
    }

    public Page<IFeedbackPageView> findPageFeedback(PageOptionsReq optionsReq){
        QFeedbackEntity feedback = new QFeedbackEntity("feedback");
        JPAQuery<IFeedbackPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                IFeedbackPageView.FeedbackPageView.class,
                feedback.as(feedback)
        )).from(feedback);
        Map<String, String> propertyPathMap = Map.of(
                "feedbackId", "id"
        );
        return DslUtil.fetchPage(em, query, optionsReq, propertyPathMap);
    }

}
