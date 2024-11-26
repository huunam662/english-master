package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    Optional<Question> findByQuestionId(UUID questionId);

    List<Question> findAllByQuestionGroup(Question question);
    Page<Question> findAllByQuestionGroupAndPart(Question question, Part part,Pageable pageable);

    List<Question> findByTopicsAndPart(Topic topic, Part part);

    Page<Question> findAll(Pageable pageable);
    int countByQuestionGroup(Question question);
    boolean existsByQuestionGroup(Question question);



}
