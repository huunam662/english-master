package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    Optional<Answer> findByAnswerId(UUID answerId);

    Optional<Answer> findByQuestionAndCorrectAnswer(Question question, boolean isCorrect);


}
