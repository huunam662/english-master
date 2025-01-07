package com.example.englishmaster_be.domain.user_answer.service;

import com.example.englishmaster_be.domain.user_answer.dto.request.UserAnswerRequest;
import com.example.englishmaster_be.model.user_answer.UserAnswerEntity;
import com.example.englishmaster_be.model.user_answer_matching.UserAnswerMatchingEntity;
import com.example.englishmaster_be.model.user_blank_answer.UserBlankAnswerEntity;
import com.example.englishmaster_be.domain.answer_blank.dto.request.AnswerBlankRequest;
import com.example.englishmaster_be.domain.answer_matching.dto.request.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.domain.user_answer.dto.request.UserAnswerRequest1;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerScoreResponse;

import java.util.List;
import java.util.UUID;

public interface IUserAnswerService {

    void createUserAnswer(List<UserAnswerRequest> request);

    UserAnswerEntity saveUserAnswer(UserAnswerRequest1 userAnswerRequest1);

    UserBlankAnswerEntity createUserBlankAnswer(AnswerBlankRequest request);

    void deleteAnswer(UUID questionId);

    boolean checkCorrectAnswerBlank(UUID questionId, UUID userId);

    int scoreAnswerBlank(UUID questionId);

    boolean checkCorrectAnswerMultipleChoice(UUID questionId, UUID userId);

    int scoreAnswerMultipleChoice(UUID questionId);

    UserAnswerScoreResponse scoreAnswerMatching(UUID questionId, UUID userId);

    int scoreAnswerMatching(UUID questionId);

    UserAnswerScoreResponse scoreAnswer(UUID questionId);

    UserAnswerMatchingEntity createUserMatchingAnswer(AnswerMatchingQuestionRequest request);
}
