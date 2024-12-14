package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.request.Answer.AnswerBlankRequest;
import com.example.englishmaster_be.entity.AnswerBlankEntity;

import java.util.List;
import java.util.UUID;

public interface IAnswerBlankService {

    List<AnswerBlankEntity> getAnswerBlankListByQuestionBlank(UUID questionId);

    AnswerBlankEntity saveAnswerBlank(AnswerBlankRequest request);

}
