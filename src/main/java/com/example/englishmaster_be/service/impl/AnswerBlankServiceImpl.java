package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.model.request.Answer.AnswerBlankRequest;
import com.example.englishmaster_be.entity.AnswerBlankEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
import com.example.englishmaster_be.repository.AnswerBlankRepository;
import com.example.englishmaster_be.service.IAnswerBlankService;
import com.example.englishmaster_be.service.IQuestionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerBlankServiceImpl implements IAnswerBlankService {

    AnswerBlankRepository answerBlankRepository;

    IQuestionService questionService;

    @Override
    public List<AnswerBlankEntity> getAnswerBlankListByQuestionBlank(UUID questionId){

        QuestionEntity question = questionService.getQuestionById(questionId);

        return answerBlankRepository.findByQuestion(question);
    }


    @Transactional
    @Override
    public AnswerBlankEntity saveAnswerBlank(AnswerBlankRequest request) {

        QuestionEntity question = questionService.getQuestionById(request.getQuestionId());

        AnswerBlankEntity answerBlank = AnswerBlankEntity.builder()
                .question(question)
                .position(request.getPosition())
                .answer(request.getContent())
                .build();

        return answerBlankRepository.save(answerBlank);
    }
}
