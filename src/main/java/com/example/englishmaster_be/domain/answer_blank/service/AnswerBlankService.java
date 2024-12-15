package com.example.englishmaster_be.domain.answer_blank.service;

import com.example.englishmaster_be.domain.answer_blank.dto.request.AnswerBlankRequest;
import com.example.englishmaster_be.model.answer.AnswerBlankEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.answer_blank.AnswerBlankRepository;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
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
public class AnswerBlankService implements IAnswerBlankService {

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
