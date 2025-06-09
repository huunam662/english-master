package com.example.englishmaster_be.domain.question.repository.jpa;

import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<QuestionEntity, UUID> {

    Optional<QuestionEntity> findByQuestionId(UUID questionId);

    List<QuestionEntity> findAllByQuestionGroupParent(QuestionEntity question);

    Page<QuestionEntity> findAllByQuestionGroupParentAndPart(QuestionEntity question, PartEntity part, Pageable pageable);

    List<QuestionEntity> findByTopicsAndPart(TopicEntity topic, PartEntity part);

    Page<QuestionEntity> findAll(Pageable pageable);

    int countByQuestionGroupParent(QuestionEntity question);

    boolean existsByQuestionGroupParent(QuestionEntity question);

    List<QuestionEntity> findByPart(PartEntity part);

    @Query("""
        SELECT DISTINCT qp FROM QuestionEntity qp
        INNER JOIN FETCH qp.part p
        LEFT JOIN FETCH qp.questionGroupChildren qc
        LEFT JOIN FETCH qc.answers
        WHERE p.partId = :partId
    """)
    List<QuestionEntity> findQuestionParentJoinChildAnswerByPart(@Param("partId") UUID partId);

    @Query("""
        SELECT qp FROM QuestionEntity qp
        INNER JOIN FETCH qp.part p
        LEFT JOIN FETCH qp.questionGroupChildren qc
        LEFT JOIN FETCH qc.answers
        WHERE p.partId = :partId
    """)
    List<QuestionEntity> findBatchQuestionParentJoinChildAnswerByPart(
            @Param("partId") UUID partId,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT qp FROM QuestionEntity qp
        WHERE qp.partId IN :partIds AND qp.isQuestionParent = TRUE
        ORDER BY qp.partId ASC
    """)
    List<QuestionEntity> findQuestionsParentsInPartIds(@Param("partIds") List<UUID> partIds);

    @Query("""
        SELECT DISTINCT qc FROM QuestionEntity qc
        WHERE qc.questionGroupId IN :questionParentIds AND qc.isQuestionParent = FALSE
    """)
    List<QuestionEntity> findQuestionsChildsInQuestionParentIds(@Param("questionParentIds") List<UUID> questionParentIds);


    @Query("""
        SELECT DISTINCT q FROM QuestionEntity q
        INNER JOIN FETCH q.contentCollection c
        WHERE q.questionId IN :questionIds
    """)
    List<QuestionEntity> findContentInQuestionIds(@Param("questionIds") List<UUID> questionIds);

    @Query("""
        SELECT DISTINCT qp FROM QuestionEntity qp
        WHERE qp.partId = :partId AND qp.isQuestionParent = TRUE
    """)
    List<QuestionEntity> findQuestionsParentByPartId(@Param("partId") UUID partId);

    @Query("""
        SELECT DISTINCT qc FROM QuestionEntity qc
        WHERE qc.questionGroupId = :questionParentId AND qc.isQuestionParent = FALSE
    """)
    List<QuestionEntity> findQuestionsChildByQuestionParentId(@Param("questionParentId") UUID questionParentId);

    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM question
        WHERE id IN :questionIds
    """, nativeQuery = true)
    void deleteAll(@Param("questionIds") List<UUID> questionIds);

}

