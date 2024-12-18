package com.example.englishmaster_be.domain.question_label.service;

import com.example.englishmaster_be.model.question_label.QuestionLabelEntity;
import com.example.englishmaster_be.domain.question_label.dto.request.QuestionLabelRequest;

import java.util.List;
import java.util.UUID;

public interface IQuestionLabelService {

    QuestionLabelEntity addLabel(QuestionLabelRequest request);

    List<QuestionLabelEntity> getLabelByIdQuestion(UUID questionId);
}
