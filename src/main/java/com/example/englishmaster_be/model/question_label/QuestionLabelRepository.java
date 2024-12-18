package com.example.englishmaster_be.model.question_label;

import com.example.englishmaster_be.model.question.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionLabelRepository extends JpaRepository<QuestionLabelEntity, UUID> {
    Optional<QuestionLabelEntity> findByLabelAndQuestion(String label, QuestionEntity question);

    List<QuestionLabelEntity> findByQuestion(QuestionEntity question);
}
