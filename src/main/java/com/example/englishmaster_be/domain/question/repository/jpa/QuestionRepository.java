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

    boolean existsByQuestionGroupParent(QuestionEntity question);

    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM question
        WHERE id IN :questionIds
    """, nativeQuery = true)
    void deleteAll(@Param("questionIds") List<UUID> questionIds);

}

