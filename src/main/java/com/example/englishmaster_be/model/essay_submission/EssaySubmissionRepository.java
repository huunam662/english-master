package com.example.englishmaster_be.model.essay_submission;

import com.example.englishmaster_be.domain.evaluator_writing.dto.WritingPartProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EssaySubmissionRepository extends JpaRepository<EssaySubmissionEntity, UUID> {

    @Query(value = """
               select tp.part_id as partId,
                      p.part_name as partName,
                      q.question_content as questionContent,
                      q.question_type as questionType,
                      sub.essay_feedback as essayFeedback
               from essay_submission sub
               join mock_test mt on mt.id = sub.mock_test_id
               join topic_part tp on tp.topic_id = mt.topic_id
               join part p on p.id = tp.part_id
               join topic_question tq on tq.topic_id = mt.topic_id
               join question q on q.id = tq.question_id
               where sub.mock_test_id = :mockTestId
        """, nativeQuery = true)
    List<WritingPartProjection> getEssaySubmissionResultForUser(@Param("mockTestId") UUID mockTestId);

    @Modifying
    @Transactional
    @Query(value = """
                insert into essay_submission(id, user_essay, essay_feedback, mock_test_id)
                values (:uuid, :userEssay, :essayFeedback, :mockTestId)
            """, nativeQuery = true)
    void createEssaySubmission(@Param("uuid") UUID uuid, @Param("userEssay") String userEssay,
                               @Param("essayFeedback") String essayFeedback,
                               @Param("mockTestId") UUID mockTestId);
}