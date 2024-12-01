package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Answer.UserAnswerRequest;
import com.example.englishmaster_be.Exception.Response.ResponseNotFoundException;
import com.example.englishmaster_be.Model.AnswerBlank;
import com.example.englishmaster_be.Model.Question;
import com.example.englishmaster_be.Model.Response.AnswerBlankResponse;
import com.example.englishmaster_be.Model.Response.QuestionBlankResponse;
import com.example.englishmaster_be.Repository.AnswerBlankRepository;
import com.example.englishmaster_be.Repository.QuestionRepository;
import com.example.englishmaster_be.Service.IAnswerBlankService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerBlankServiceImpl implements IAnswerBlankService {

    AnswerBlankRepository repository;

    QuestionRepository questionRepository;

    @Override
    public List<QuestionBlankResponse> getAnswerWithQuestionBlank(UUID questionId){
        Question question=questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        ()-> new ResponseNotFoundException("Not found Question with"+ questionId)
                );

        List<AnswerBlank> questionBlanks=repository.findByQuestion(question);

        List<QuestionBlankResponse> questionBlankResponses = new ArrayList<>();

        questionBlanks.forEach(questionBlankItem -> {

            QuestionBlankResponse questionBlank = QuestionBlankResponse.builder()
                    .position(questionBlankItem.getPosition())
                    .answer(questionBlankItem.getAnswer())
                    .build();

            questionBlankResponses.add(questionBlank);

        });

       return questionBlankResponses;
    }


    @Override
    @Transactional
    public AnswerBlankResponse createAnswerBlank(UserAnswerRequest request) {

        Question question = questionRepository.findByQuestionId(request.getQuestionId())
                .orElseThrow(
                        ()-> new ResponseNotFoundException("Not found question with"+ request.getQuestionId())
                );

        AnswerBlank answerBlank = AnswerBlank.builder()
                .question(question)
                .position(request.getPosition())
                .answer(request.getContent())
                .build();

        answerBlank = repository.save(answerBlank);

        return new AnswerBlankResponse(answerBlank);
    }
}
