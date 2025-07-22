package com.example.englishmaster_be.domain.mock_test.mock_test.repository;

import com.example.englishmaster_be.domain.mock_test.mock_test.dto.view.IMockTestToUserView;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MockTestRepository extends JpaRepository<MockTestEntity, UUID> {

    Page<MockTestEntity> findAll(Pageable pageable);

    List<MockTestEntity> findAllByTopic(TopicEntity topic);

    Optional<MockTestEntity> findByMockTestId(UUID mockTestId);

    @Query(value = "SELECT p FROM MockTestEntity p WHERE " +
            "(:year IS NULL OR YEAR(p.createAt) = :year) AND " +
            "(:month IS NULL OR MONTH(p.createAt) = :month) AND " +
            "(:day IS NULL OR DAY(p.createAt) = :day) AND "+
            "p.topic = :topic")
    List<MockTestEntity> findAllByYearMonthAndDay(
            @Param("year") String year,
            @Param("month") String month,
            @Param("day") String day,
            @Param("topic") TopicEntity topic
    );

    @Query(value = "SELECT p FROM MockTestEntity p WHERE " +
            "(:year IS NULL OR YEAR(p.createAt) = :year) AND " +
            "(:month IS NULL OR MONTH(p.createAt) = :month) AND " +
            "p.topic = :topic")
    List<MockTestEntity> findAllByYearMonth(
            @Param("year") String year,
            @Param("month") String month,
            @Param("topic") TopicEntity topic);

    @Query(value = "SELECT p FROM MockTestEntity p WHERE " +
            "(:year IS NULL OR YEAR(p.createAt) = :year) AND " +
            "p.topic = :topic")
    List<MockTestEntity> findAllByYear(
            @Param("year") String year,
            @Param("topic") TopicEntity topic
    );

    @Query(value = """
            select
                mt.id as mockTestId, t.id as topicId, t.topic_name as testName,
                coalesce(mt.total_score, 0) as totalScore, tt.type_name as testType,
                mt.create_at as examStartTime
            from mock_test mt
            join topics t on t.id = mt.topic_id
            join topic_type tt on tt.id = t.topic_type_id
            where mt.user_id = :userId
            """, nativeQuery = true)
    List<IMockTestToUserView> findExamResultForUser(@Param("userId") UUID userId);

    @Query("""
        SELECT mockTest FROM MockTestEntity mockTest
        INNER JOIN FETCH mockTest.user userCreate
        INNER JOIN FETCH mockTest.topic topicTest
        LEFT JOIN FETCH topicTest.topicType
        WHERE mockTest.mockTestId = :mockTestId
    """)
    Optional<MockTestEntity> findMockTestById(@Param("mockTestId") UUID mockTestId);

    @Query("""
        SELECT mockTest FROM MockTestEntity mockTest
        INNER JOIN FETCH mockTest.user userCreate
        INNER JOIN FETCH mockTest.topic topicTest
        LEFT JOIN FETCH topicTest.topicType
        WHERE mockTest.mockTestId = :mockTestId
    """)
    Optional<MockTestEntity> findMockTestJoinUserTopicTopicType(@Param("mockTestId") UUID mockTestId);

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE mock_test
        SET answers_correct_percent = :answersCorrectPercent,
            total_answers_correct = :totalAnswersCorrect,
            total_answers_wrong = :totalAnswersWrong,
            total_questions_finish = :totalQuestionsFinish,
            total_questions_skip = :totalQuestionsSkip,
            total_score = :totalScore,
            update_at = now()
        WHERE id = :mockTestId
    """, nativeQuery = true)
    void updateMockTest(
        @Param("mockTestId") UUID mockTestId,
        @Param("answersCorrectPercent") float answersCorrectPercent,
        @Param("totalAnswersCorrect") int totalAnswersCorrect,
        @Param("totalAnswersWrong") int totalAnswersWrong,
        @Param("totalQuestionsFinish") int totalQuestionsFinish,
        @Param("totalQuestionsSkip") int totalQuestionsSkip,
        @Param("totalScore") int totalScore
    );

    @Query("""
        SELECT mt FROM MockTestEntity mt
        INNER JOIN FETCH mt.topic t
        INNER JOIN FETCH mt.user u
        LEFT JOIN FETCH mt.readingListeningSubmissions rls
        LEFT JOIN FETCH rls.answerChoice ac
        LEFT JOIN FETCH ac.question qc
        LEFT JOIN FETCH qc.part
        WHERE mt.mockTestId = :mockTestId
    """)
    MockTestEntity findMockTestJoinTopicUserResultPart(@Param("mockTestId") UUID mockTestId);

    @Query(value = """
        SELECT EXISTS(SELECT id FROM mock_test WHERE id = :mockTestId) 
    """, nativeQuery = true)
    Boolean isExistedMockTest(@Param("mockTestId") UUID mockTestId);
}
