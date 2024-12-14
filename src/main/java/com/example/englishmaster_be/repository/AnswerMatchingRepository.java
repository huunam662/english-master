package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.entity.AnswerMatchingEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerMatchingRepository extends JpaRepository<AnswerMatchingEntity, UUID> {
    List<AnswerMatchingEntity> findAllByQuestion(QuestionEntity question);
}
