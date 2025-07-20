package com.example.englishmaster_be.domain.exam.answer.service;


import com.example.englishmaster_be.domain.exam.answer.dto.req.AnswerReq;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IAnswerService {

    AnswerEntity saveAnswer(AnswerReq answerRequest);

    boolean existQuestion(AnswerEntity answer, QuestionEntity question);

    AnswerEntity getAnswerById(UUID answerID);

    void deleteAnswer(UUID answerId);

    AnswerEntity correctAnswer(QuestionEntity question);

    List<AnswerEntity> getListAnswerByQuestionId(UUID questionId);

    AnswerEntity save(AnswerEntity answer);

    List<AnswerEntity> saveAll(Set<AnswerEntity> answers);
}
