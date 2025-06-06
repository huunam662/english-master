package com.example.englishmaster_be.domain.topic.repository.jpa;

import com.example.englishmaster_be.domain.topic.dto.projection.INumberAndScoreQuestionTopic;
import com.example.englishmaster_be.domain.topic.dto.projection.ITopicKeyProjection;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

public interface TopicRepository extends JpaRepository<TopicEntity, UUID>, JpaSpecificationExecutor<TopicEntity> {

    Page<TopicEntity> findAll(Pageable pageable);

    List<TopicEntity> findAllByPack(PackEntity pack);

    @Query("SELECT t FROM TopicEntity t WHERE LOWER(t.topicName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TopicEntity> findTopicsByQuery(@Param("query") String query, Pageable pageable);

    @Query("SELECT t FROM TopicEntity t WHERE t.topicName = :topicName")
    Optional<TopicEntity> findByTopicName(@Param("topicName") String topicName);

    @Query("SELECT t FROM TopicEntity t WHERE FUNCTION('DATE', t.startTime) = FUNCTION('DATE', :startTime)")
    List<TopicEntity> findByStartTime(@Param("startTime") LocalDateTime startTime);

    Optional<TopicEntity> findByTopicId(UUID topicId);

    @Query("SELECT t.topicImage FROM TopicEntity t order by t.topicId")
    List<String> findAllTopicImages();

    @Query(value = """
        SELECT DISTINCT t FROM TopicEntity t
        LEFT JOIN FETCH t.parts p
        LEFT JOIN FETCH p.questions qp
        LEFT JOIN FETCH qp.questionGroupChildren qgc
        LEFT JOIN FETCH qgc.answers a
        WHERE t.topicId = :topicId
            AND LOWER(p.partName) = LOWER(:partName)
            AND qp.isQuestionParent = TRUE
            AND qgc.isQuestionParent = FALSE
    """)
    Optional<TopicEntity> findTopicQuestionsFromTopicAndPart(@Param("topicId") UUID topicId, @Param("partName") String partName);

    @Query(value = """
        SELECT DISTINCT t FROM TopicEntity t
        LEFT JOIN FETCH t.parts p
        LEFT JOIN FETCH p.questions qp
        LEFT JOIN FETCH qp.questionGroupChildren qgc
        LEFT JOIN FETCH qgc.answers a
        WHERE t.topicId = :topicId
            AND qp.isQuestionParent = TRUE
            AND qgc.isQuestionParent = FALSE
    """)
    Optional<TopicEntity> findTopicQuestionsFromTopic(@Param("topicId") UUID topicId);

    @Query(value = """
        SELECT COUNT(qc.id) as numberQuestions, COALESCE(SUM(qc.question_score), 0) as scoreQuestions
        FROM topics t
                 INNER JOIN topic_part tp ON tp.topic_id = t.id
                 INNER JOIN part p ON tp.part_id = p.id
                 INNER JOIN question qc ON p.id = qc.part_id
        WHERE qc.is_question_parent = FALSE AND t.id = :topicId
    """, nativeQuery = true)
    INumberAndScoreQuestionTopic findNumberAndScoreQuestions(@Param("topicId") UUID topicId);

    @Query("""
        SELECT DISTINCT t.topicId as topicId, t.packId as packId
        FROM TopicEntity t
        WHERE LOWER(t.topicName) = LOWER(:topicName)
    """)
    ITopicKeyProjection findTopicIdByName(@Param("topicName") String topicName);

}

