package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Request.Answer.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.entity.AnswerMatchingEntity;
import com.example.englishmaster_be.Model.Response.AnswerMatchingBasicResponse;

import java.util.List;
import java.util.UUID;

public interface IAnswerMatching {

    AnswerMatchingEntity saveAnswerMatching(AnswerMatchingQuestionRequest answerMatchingQuestionRequest);

    List<AnswerMatchingBasicResponse> getListAnswerMatchingWithShuffle(UUID questionId);
}
