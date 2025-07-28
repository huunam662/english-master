package com.example.englishmaster_be.domain.mock_test.mock_test.repository;

import com.example.englishmaster_be.common.constant.Role;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.pack.pack.model.QPackEntity;
import com.example.englishmaster_be.domain.exam.pack.type.model.QPackTypeEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.QTopicEntity;
import com.example.englishmaster_be.domain.exam.topic.type.model.QTopicTypeEntity;
import com.example.englishmaster_be.domain.mock_test.evaluator_writing.model.QEssaySubmissionEntity;
import com.example.englishmaster_be.domain.mock_test.mock_test.dto.view.IMockTestPageView;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.QMockTestEntity;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.model.QReadingListeningSubmissionEntity;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.QSpeakingSubmissionEntity;
import com.example.englishmaster_be.domain.user.user.model.QUserEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.util.DslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class MockTestDslRepository {

    @PersistenceContext
    private final EntityManager em;

    private final IUserService userService;

    @Lazy
    public MockTestDslRepository(EntityManager em, IUserService userService) {
        this.em = em;
        this.userService = userService;
    }

    public Page<IMockTestPageView> findPageMockTestUser(PageOptionsReq optionsReq){
        UserEntity currentUser = userService.currentUser();
        QMockTestEntity mockTest = new QMockTestEntity("mockTest");
        QUserEntity user = QUserEntity.userEntity;
        QTopicEntity topic = QTopicEntity.topicEntity;
        QTopicTypeEntity topicType = QTopicTypeEntity.topicTypeEntity;
        QPackEntity pack = QPackEntity.packEntity;
        QPackTypeEntity packType = QPackTypeEntity.packTypeEntity;
        QEssaySubmissionEntity essaySubmission = QEssaySubmissionEntity.essaySubmissionEntity;
        QReadingListeningSubmissionEntity readingListeningSubmission = QReadingListeningSubmissionEntity.readingListeningSubmissionEntity;
        QSpeakingSubmissionEntity speakingSubmission = QSpeakingSubmissionEntity.speakingSubmissionEntity;
        String countEssay = "countQuestionEssay";
        String countReadingOrListening = "countQuestionReadingOrListening";
        String countSpeaking =  "countQuestionSpeaking";
        Map<String, NumberExpression<?>> numberExpressionMap = Map.of(
                countEssay, essaySubmission.countDistinct(),
                countSpeaking, speakingSubmission.countDistinct(),
                countReadingOrListening, readingListeningSubmission.countDistinct()
        );
        JPAQuery<IMockTestPageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
            IMockTestPageView.MockTestPageView.class,
            mockTest.as(mockTest),
            numberExpressionMap.get(countEssay).as(countEssay),
            numberExpressionMap.get(countSpeaking).as(countSpeaking),
            numberExpressionMap.get(countReadingOrListening).as(countReadingOrListening)
        ))
        .from(mockTest)
        .leftJoin(mockTest.user, user).fetchJoin()
        .leftJoin(mockTest.topic, topic).fetchJoin()
        .leftJoin(topic.topicType, topicType).fetchJoin()
        .leftJoin(topic.pack, pack).fetchJoin()
        .leftJoin(pack.packType, packType).fetchJoin()
        .leftJoin(mockTest.essaySubmissions, essaySubmission)
        .leftJoin(mockTest.readingListeningSubmissions, readingListeningSubmission)
        .leftJoin(mockTest.speakingSubmissions, speakingSubmission)
        .groupBy(mockTest, user, topic, topicType, pack, packType);
        if(currentUser.getRole().getRoleName().equals(Role.USER)){
            query.where(mockTest.user.eq(currentUser));
        }
        return DslUtil.fetchPage(em, query, optionsReq);
    }

}
