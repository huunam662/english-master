package com.example.englishmaster_be.domain.question_label.service;

import com.example.englishmaster_be.model.question_label.QuestionLabelEntity;
import com.example.englishmaster_be.domain.question_label.dto.request.QuestionLabelRequest;
import com.example.englishmaster_be.model.question_label.QuestionLabelRepository;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionLabelService implements IQuestionLabelService {

    QuestionLabelRepository labelRepository;

    IQuestionService questionService;

    @Transactional
    @Override
    public QuestionLabelEntity addLabel(QuestionLabelRequest request) {
        QuestionEntity questionEntity = questionService.getQuestionById(request.getQuestionId());

        QuestionLabelEntity labelEntity=labelRepository.findByLabelAndQuestion(request.getLabel(),questionEntity).orElse(new QuestionLabelEntity());

        labelEntity.setQuestion(questionEntity);
        labelEntity.setLabel(request.getLabel());
        labelEntity.setContent(request.getTitle());
        return labelRepository.save(labelEntity);
    }

    @Override
    public List<QuestionLabelEntity> getLabelByIdQuestion(UUID questionId) {
        QuestionEntity questionEntity = questionService.getQuestionById(questionId);

        return labelRepository.findByQuestion(questionEntity);
    }
}
