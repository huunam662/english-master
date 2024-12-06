package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    Optional<Answer> findByAnswerId(UUID answerId);

    Optional<Answer> findByQuestionAndCorrectAnswer(Question question, boolean isCorrect);

    List<Answer> findByQuestion(Question question);
}
