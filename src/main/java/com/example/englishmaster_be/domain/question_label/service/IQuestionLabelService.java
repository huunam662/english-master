package com.example.englishmaster_be.domain.question_label.service;

import com.example.englishmaster_be.model.question_label.QuestionLabelEntity;
import com.example.englishmaster_be.domain.question_label.dto.request.QuestionLabelRequest;

public interface IQuestionLabelService {

    QuestionLabelEntity addLabel(QuestionLabelRequest request);
}
