package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.model.request.Answer.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.entity.AnswerMatchingEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
import com.example.englishmaster_be.model.response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.repository.AnswerMatchingRepository;
import com.example.englishmaster_be.service.IAnswerMatching;
import com.example.englishmaster_be.service.IQuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerMatchingServiceImpl implements IAnswerMatching {

    AnswerMatchingRepository answerMatchingRepository;

    IQuestionService questionService;


    @Override
    public AnswerMatchingEntity saveAnswerMatching(AnswerMatchingQuestionRequest answerMatchingQuestionRequest) {

        QuestionEntity question = questionService.getQuestionById(answerMatchingQuestionRequest.getQuestionId());

        AnswerMatchingEntity answerMatching = new AnswerMatchingEntity();
        answerMatching.setQuestion(question);
        answerMatching.setContentLeft(answerMatchingQuestionRequest.getContentLeft());
        answerMatching.setContentRight(answerMatchingQuestionRequest.getContentRight());

        return answerMatchingRepository.save(answerMatching);
    }

    @Override
    public List<AnswerMatchingBasicResponse> getListAnswerMatchingWithShuffle(UUID questionId) {

        QuestionEntity question = questionService.getQuestionById(questionId);

        List<AnswerMatchingEntity> answerMatchingList = answerMatchingRepository.findAllByQuestion(question);

        List<String> contentLeft = answerMatchingList.stream().map(AnswerMatchingEntity::getContentLeft).collect(Collectors.toList());
        List<String> contentRight = answerMatchingList.stream().map(AnswerMatchingEntity::getContentRight).collect(Collectors.toList());

        Collections.shuffle(contentLeft);
        Collections.shuffle(contentRight);

        List<AnswerMatchingBasicResponse> answerMatchingShuffleResponseList = new ArrayList<>();

        for(int i = 0; i < contentLeft.size(); i++)
            answerMatchingShuffleResponseList.add(
                    AnswerMatchingBasicResponse.builder()
                            .contentLeft(contentLeft.get(i))
                            .contentRight(contentRight.get(i))
                            .build()
            );

        return answerMatchingShuffleResponseList;

    }
}
