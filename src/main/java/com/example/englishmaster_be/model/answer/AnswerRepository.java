package com.example.englishmaster_be.model.answer;

import com.example.englishmaster_be.model.question.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, UUID> {

    Optional<AnswerEntity> findByAnswerId(UUID answerId);

    Optional<AnswerEntity> findByQuestionAndCorrectAnswer(QuestionEntity question, boolean isCorrect);

    List<AnswerEntity> findByQuestion(QuestionEntity question);
}
