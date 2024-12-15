package com.example.englishmaster_be.model.answer_blank;

import com.example.englishmaster_be.model.answer.AnswerBlankEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface AnswerBlankRepository extends JpaRepository<AnswerBlankEntity, UUID> {


    List<AnswerBlankEntity> findByQuestion(QuestionEntity question);



}
