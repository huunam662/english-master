package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.request.Question.LabelRequest;
import com.example.englishmaster_be.entity.LabelEntity;
import com.example.englishmaster_be.entity.QuestionEntity;

public interface ILabelService {

    LabelEntity addLabel(LabelRequest request);
}
