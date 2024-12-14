package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.entity.AnswerBlankEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface AnswerBlankRepository extends JpaRepository<AnswerBlankEntity, UUID> {


    List<AnswerBlankEntity> findByQuestion(QuestionEntity question);



}
