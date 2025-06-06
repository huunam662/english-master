package com.example.englishmaster_be.domain.answer.service;


import com.example.englishmaster_be.domain.answer.dto.request.AnswerRequest;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IAnswerService {

    AnswerEntity saveAnswer(AnswerRequest answerRequest);

    boolean existQuestion(AnswerEntity answer, QuestionEntity question);

    AnswerEntity getAnswerById(UUID answerID);

    void deleteAnswer(UUID answerId);

    AnswerEntity correctAnswer(QuestionEntity question);

    AnswerEntity choiceAnswer(QuestionEntity question, MockTestEntity mockTest);

    List<AnswerEntity> getListAnswerByQuestionId(UUID questionId);

    AnswerEntity save(AnswerEntity answer);

    List<AnswerEntity> saveAll(Set<AnswerEntity> answers);
}
