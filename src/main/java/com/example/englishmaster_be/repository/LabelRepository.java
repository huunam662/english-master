package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.entity.LabelEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<LabelEntity, UUID> {
    Optional<LabelEntity> findByLabelAndQuestion(String label, QuestionEntity question);
}
