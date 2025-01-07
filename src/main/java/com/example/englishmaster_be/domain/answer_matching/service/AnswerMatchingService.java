package com.example.englishmaster_be.domain.answer_matching.service;

import com.example.englishmaster_be.domain.answer_matching.dto.request.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMatchingDto;
import com.example.englishmaster_be.model.answer_matching.AnswerMatchingEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.model.answer_matching.AnswerMatchingRepository;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
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
public class AnswerMatchingService implements IAnswerMatchingService {

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

    @Override
    public QuestionMatchingDto getListAnswerMatchingWithShuffle1(UUID questionId) {

        QuestionEntity question = questionService.getQuestionById(questionId);

        List<AnswerMatchingEntity> answerMatchingList = answerMatchingRepository.findAllByQuestion(question);

        List<String> contentLeft = answerMatchingList.stream().map(AnswerMatchingEntity::getContentLeft).collect(Collectors.toList());
        List<String> contentRight = answerMatchingList.stream().map(AnswerMatchingEntity::getContentRight).collect(Collectors.toList());

        Collections.shuffle(contentLeft);
        Collections.shuffle(contentRight);

        contentRight=contentRight.stream().filter(s-> !Objects.isNull(s)).toList();
        QuestionMatchingDto questionMatchingDto = new QuestionMatchingDto();
        questionMatchingDto.setQuestionId(questionId);
        questionMatchingDto.setContentLeft(contentLeft);
        questionMatchingDto.setContentRight(contentRight);
        questionMatchingDto.setType(question.getQuestionType());

        return questionMatchingDto;

    }
}
