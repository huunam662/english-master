package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Request.Question.LabelRequest;
import com.example.englishmaster_be.entity.LabelEntity;
import com.example.englishmaster_be.entity.QuestionEntity;

public interface ILabelService {

    LabelEntity addLabel(LabelRequest request);
}
