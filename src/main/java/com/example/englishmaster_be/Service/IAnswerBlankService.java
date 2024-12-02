package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Answer.UserAnswerRequest;
import com.example.englishmaster_be.Model.Response.AnswerBlankResponse;
import com.example.englishmaster_be.Model.Response.QuestionBlankResponse;

import java.util.List;
import java.util.UUID;

public interface IAnswerBlankService {

    List<QuestionBlankResponse> getAnswerWithQuestionBlank(UUID questionId);

    AnswerBlankResponse createAnswerBlank(UserAnswerRequest request);

}
