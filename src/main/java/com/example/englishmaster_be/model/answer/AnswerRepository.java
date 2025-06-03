package com.example.englishmaster_be.model.answer;

import com.example.englishmaster_be.model.question.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
        INNER JOIN FETCH p.topics t
        WHERE t.topicId = :topicId
        AND ac.answerId IN :answerIds
    """)
    List<AnswerEntity> findAnswersInAnswerIds(@Param("topicId") UUID topicId, @Param("answerIds") List<UUID> answerIds);

    @Query("""
        SELECT DISTINCT ac FROM AnswerEntity ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.part qcp
        INNER JOIN FETCH qc.questionGroupParent qp
        INNER JOIN FETCH qp.part qpp
        INNER JOIN FETCH qpp.topics t
        WHERE t.topicId = :topicId
    """)
    List<AnswerEntity> findAnswersJoinQuestionPartTopic(@Param("topicId") UUID topicId);

    @Query("""
        SELECT DISTINCT ac FROM AnswerEntity ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.part qcp
        INNER JOIN FETCH qc.questionGroupParent qp
        INNER JOIN FETCH qp.part qpp
        INNER JOIN FETCH qpp.topics t
        WHERE t.topicId = :topicId AND LOWER(qpp.partName) = LOWER(:partName) 
    """)
    List<AnswerEntity> findAnswersJoinQuestionPartTopic(@Param("topicId") UUID topicId, @Param("partName") String partName);

    @Query("""
        SELECT DISTINCT ac FROM AnswerEntity ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.part qcp
        INNER JOIN FETCH qc.questionGroupParent qp
        INNER JOIN FETCH qp.part qpp
        INNER JOIN FETCH qpp.topics t
        WHERE t.topicId = :topicId AND qpp.partId = :partId
    """)
    List<AnswerEntity> findAnswersJoinQuestionPartTopic(@Param("topicId") UUID topicId, @Param("partId") UUID partId);

    @Query("""
        SELECT DISTINCT ac FROM AnswerEntity ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.questionGroupParent qp
        WHERE qp.partId = :partId
    """)
    List<AnswerEntity> findAnswersJoinQuestionPartByPartId(@Param("partId") UUID partId);
}
