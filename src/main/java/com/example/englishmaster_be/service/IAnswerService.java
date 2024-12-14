package com.example.englishmaster_be.service;


import com.example.englishmaster_be.model.request.Answer.AnswerRequest;
import com.example.englishmaster_be.entity.AnswerEntity;
import com.example.englishmaster_be.entity.MockTestEntity;
import com.example.englishmaster_be.entity.QuestionEntity;

import java.util.List;
import java.util.UUID;

public interface IAnswerService {

    AnswerEntity saveAnswer(AnswerRequest answerRequest);

    boolean existQuestion(AnswerEntity answer, QuestionEntity question);

    AnswerEntity getAnswerById(UUID answerID);

    void deleteAnswer(UUID answerId);

    AnswerEntity correctAnswer(QuestionEntity question);

    AnswerEntity choiceAnswer(QuestionEntity question, MockTestEntity mockTest);

    List<AnswerEntity> getListAnswerByQuestionId(UUID questionId);
}
