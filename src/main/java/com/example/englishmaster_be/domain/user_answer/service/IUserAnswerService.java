package com.example.englishmaster_be.domain.user_answer.service;

import com.example.englishmaster_be.domain.user_answer.dto.request.UserAnswerRequest;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerResponse;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerScoreResponse;

import java.util.List;
import java.util.UUID;

public interface IUserAnswerService {

    UserAnswerResponse createUserAnswer(List<UserAnswerRequest> request);


    void deleteAnswer(UUID questionId);

    boolean checkCorrectAnswerBlank(UUID questionId, UUID userId);

    int scoreAnswerBlank(UUID questionId);

    boolean checkCorrectAnswerMultipleChoice(UUID questionId, UUID userId);

    int scoreAnswerMultipleChoice(UUID questionId);

    UserAnswerScoreResponse scoreAnswerMatching(UUID questionId, UUID userId);

    int scoreAnswerMatching(UUID questionId);

    UserAnswerScoreResponse scoreAnswer(UUID questionId);

}
