package com.example.englishmaster_be.domain.answer_matching.service;

import com.example.englishmaster_be.domain.answer_matching.dto.request.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.model.answer_matching.AnswerMatchingEntity;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;

import java.util.List;
import java.util.UUID;

public interface IAnswerMatchingService {

    AnswerMatchingEntity saveAnswerMatching(AnswerMatchingQuestionRequest answerMatchingQuestionRequest);

    List<AnswerMatchingBasicResponse> getListAnswerMatchingWithShuffle(UUID questionId);
}
