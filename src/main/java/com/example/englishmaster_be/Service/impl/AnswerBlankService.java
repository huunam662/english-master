package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Answer.UserAnswerRequest;
import com.example.englishmaster_be.Exception.Response.ResponseNotFoundException;
import com.example.englishmaster_be.Model.AnswerBlank;
import com.example.englishmaster_be.Model.Question;
import com.example.englishmaster_be.Model.Response.QuestionBlankResponse;
import com.example.englishmaster_be.Repository.AnswerBlankRepository;
import com.example.englishmaster_be.Repository.QuestionRepository;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerBlankService {

    AnswerBlankRepository repository;

    QuestionRepository questionRepository;

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

    @Transactional
    public void createAnswerBlank(UserAnswerRequest request) {
        Question question=questionRepository.findByQuestionId(request.getQuestionId())
                .orElseThrow(
                        ()-> new ResponseNotFoundException("Not found question with"+ request.getQuestionId())
                );
        AnswerBlank answerBlank=new AnswerBlank();
        answerBlank.setQuestion(question);
        answerBlank.setPosition(request.getPosition());
        answerBlank.setAnswer(request.getContent());
        repository.save(answerBlank);

    }
}
