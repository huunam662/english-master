package com.example.englishmaster_be.domain.answer.repository.jpa;

import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, UUID> {

    Optional<AnswerEntity> findByAnswerId(UUID answerId);

    Optional<AnswerEntity> findByQuestionAndCorrectAnswer(QuestionEntity question, boolean isCorrect);

    List<AnswerEntity> findByQuestion(QuestionEntity question);

    AnswerEntity findByQuestionAndAnswerContent(QuestionEntity question, String answerContent);

    @Query("""
        SELECT DISTINCT a FROM AnswerEntity a
        WHERE a.questionChildId IN :questionIds
        ORDER BY a.questionChildId ASC
    """)
    List<AnswerEntity> findAnswersInQuestionIds(@Param("questionIds") List<UUID> questionIds);

    @Query("""
        SELECT DISTINCT ac FROM AnswerEntity ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.part p
        INNER JOIN FETCH p.topic t
        WHERE t.topicId = :topicId
        AND ac.answerId IN :answerIds
    """)
    List<AnswerEntity> findAnswersInAnswerIds(@Param("topicId") UUID topicId, @Param("answerIds") List<UUID> answerIds);

    @Query("""
        SELECT DISTINCT ac FROM AnswerEntity ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.questionGroupParent qp
        INNER JOIN FETCH qp.part qpp
        INNER JOIN FETCH qpp.topic t
        WHERE t.topicId = :topicId
    """)
    List<AnswerEntity> findAnswersJoinQuestionPartTopic(@Param("topicId") UUID topicId);

    @Query("""
        SELECT DISTINCT ac FROM AnswerEntity ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.questionGroupParent qp
        INNER JOIN FETCH qp.part qpp
        INNER JOIN FETCH qpp.topic t
        WHERE t.topicId IN :topicIds
    """)
    List<AnswerEntity> findAnswersJoinQuestionPartTopicIn(@Param("topicIds") List<UUID> topicIds);


    @Query("""
        SELECT DISTINCT ac FROM AnswerEntity ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.questionGroupParent qp
        INNER JOIN FETCH qp.part qpp
        INNER JOIN FETCH qpp.topic t
        WHERE t.topicId = :topicId AND LOWER(qpp.partName) = LOWER(:partName)
    """)
    List<AnswerEntity> findAnswersJoinQuestionPartTopic(@Param("topicId") UUID topicId, @Param("partName") String partName);

    @Query("""
        SELECT DISTINCT ac FROM AnswerEntity ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.questionGroupParent qp
        INNER JOIN FETCH qp.part qpp
        INNER JOIN FETCH qpp.topic t
        WHERE t.topicId = :topicId
    """)
    List<AnswerEntity> findAnswersJoinQuestionPartTopic(@Param("topicId") UUID topicId, @Param("partId") UUID partId);

    @Query("""
        SELECT DISTINCT ac FROM AnswerEntity ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.questionGroupParent qp
        WHERE qp.partId = :partId
    """)
    List<AnswerEntity> findAnswersJoinQuestionPartByPartId(@Param("partId") UUID partId);

    @Query(value = """
        SELECT id FROM answer
        WHERE question_id IN :questionIds
    """, nativeQuery = true)
    List<UUID> findAllAnswerIdsIn(@Param("questionIds") List<UUID> questionIds);

    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM answer
        WHERE id IN :answerIds
    """, nativeQuery = true)
    void deleteAll(@Param("answerIds") List<UUID> answerIds);

}
