package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.request.Answer.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.entity.AnswerMatchingEntity;
import com.example.englishmaster_be.model.response.AnswerMatchingBasicResponse;

import java.util.List;
import java.util.UUID;

public interface IAnswerMatching {

    AnswerMatchingEntity saveAnswerMatching(AnswerMatchingQuestionRequest answerMatchingQuestionRequest);

    List<AnswerMatchingBasicResponse> getListAnswerMatchingWithShuffle(UUID questionId);
}
