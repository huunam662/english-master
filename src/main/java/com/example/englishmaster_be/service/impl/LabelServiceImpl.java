package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.model.request.Question.LabelRequest;
import com.example.englishmaster_be.repository.LabelRepository;
import com.example.englishmaster_be.service.ILabelService;
import com.example.englishmaster_be.entity.LabelEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
import com.example.englishmaster_be.entity.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LabelServiceImpl implements ILabelService {
    private final LabelRepository labelRepository;
    private final QuestionServiceImpl questionService;

    @Transactional
    @Override
    public LabelEntity addLabel(LabelRequest request) {
        QuestionEntity questionEntity = questionService.getQuestionById(request.getQuestionId());

        LabelEntity labelEntity=labelRepository.findByLabelAndQuestion(request.getLabel(),questionEntity).orElse(new LabelEntity());

        labelEntity.setQuestion(questionEntity);
        labelEntity.setLabel(request.getLabel());
        labelEntity.setContent(request.getTitle());
        return labelRepository.save(labelEntity);
    }
}
