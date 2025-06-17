package com.example.englishmaster_be.domain.mock_test.repository.jpa;

import com.example.englishmaster_be.domain.mock_test.dto.response.IMockTestToUserResponse;
import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
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
                mt.total_score as totalScore, t.topic_type as testType,
                mt.create_at as examStartTime
            from mock_test mt
            join topics t on t.id = mt.topic_id
            where mt.user_id = :userId
            """, nativeQuery = true)
    List<IMockTestToUserResponse> findExamResultForUser(@Param("userId") UUID userId);

    @Query("""
        SELECT mockTest FROM MockTestEntity mockTest
        INNER JOIN FETCH mockTest.user userCreate
        INNER JOIN FETCH mockTest.topic topicTest
        WHERE mockTest.mockTestId = :mockTestId
    """)
    Optional<MockTestEntity> findMockTestById(@Param("mockTestId") UUID mockTestId);

    @Query("""
        SELECT mockTest FROM MockTestEntity mockTest
        INNER JOIN FETCH mockTest.user userCreate
        INNER JOIN FETCH mockTest.topic topicTest
        INNER JOIN FETCH topicTest.topicType
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
        LEFT JOIN FETCH mt.mockTestResults mtr
        LEFT JOIN FETCH mtr.part
        WHERE mt.mockTestId = :mockTestId
    """)
    MockTestEntity findMockTestJoinTopicUserResultPart(@Param("mockTestId") UUID mockTestId);

    @Query(value = """
        SELECT EXISTS(SELECT id FROM mock_test WHERE id = :mockTestId) 
    """, nativeQuery = true)
    Boolean isExistedMockTest(@Param("mockTestId") UUID mockTestId);
}
