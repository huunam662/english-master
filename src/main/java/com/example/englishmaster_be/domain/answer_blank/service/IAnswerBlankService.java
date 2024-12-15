package com.example.englishmaster_be.domain.answer_blank.service;

import com.example.englishmaster_be.domain.answer_blank.dto.request.AnswerBlankRequest;
import com.example.englishmaster_be.model.answer.AnswerBlankEntity;

import java.util.List;
import java.util.UUID;

public interface IAnswerBlankService {

    List<AnswerBlankEntity> getAnswerBlankListByQuestionBlank(UUID questionId);

    AnswerBlankEntity saveAnswerBlank(AnswerBlankRequest request);

}
