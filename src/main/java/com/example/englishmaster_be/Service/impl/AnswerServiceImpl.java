package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Request.Answer.AnswerRequest;
import com.example.englishmaster_be.Exception.template.CustomException;
import com.example.englishmaster_be.Exception.template.BadRequestException;
import com.example.englishmaster_be.Mapper.AnswerMapper;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IAnswerService;
import com.example.englishmaster_be.Service.IQuestionService;
import com.example.englishmaster_be.Service.IUserService;
import com.example.englishmaster_be.entity.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.englishmaster_be.Common.enums.error.ErrorEnum;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerServiceImpl implements IAnswerService {

    AnswerRepository answerRepository;

    DetailMockTestRepository detailMockTestRepository;

    IUserService userService;

    IQuestionService questionService;


    @Transactional
    @Override
    public AnswerEntity saveAnswer(AnswerRequest answerRequest) {

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(answerRequest.getQuestionId());

        boolean questionHadCorrectAnswer = question.getAnswers().stream().anyMatch(
                answer -> {
                    if(
                            Objects.nonNull(answerRequest.getAnswerId())
                            && answer.getCorrectAnswer()
                            && answerRequest.getCorrectAnswer()
                            && !answer.getAnswerId().equals(answerRequest.getAnswerId())
                    ) return true;

                    else return answer.getCorrectAnswer() && answerRequest.getCorrectAnswer();
                }
        );

        if (questionHadCorrectAnswer) throw new BadRequestException("Had correct AnswerEntity");

        AnswerEntity answer;

        if(answerRequest.getAnswerId() != null)
            answer = getAnswerById(answerRequest.getAnswerId());

        else answer = AnswerEntity.builder()
                .createAt(LocalDateTime.now())
                .userCreate(user)
                .build();

        answer.setQuestion(question);
        answer.setUpdateAt(LocalDateTime.now());
        answer.setUserUpdate(user);

        AnswerMapper.INSTANCE.flowToAnswerEntity(answerRequest, answer);

        return answerRepository.save(answer);
    }

    @Override
    public boolean existQuestion(AnswerEntity answer, QuestionEntity question) {
        return answer.getQuestion().equals(question);
    }

    @Override
    public AnswerEntity getAnswerById(UUID answerID) {

        return answerRepository.findByAnswerId(answerID)
                .orElseThrow(() -> new CustomException(ErrorEnum.ANSWER_NOT_FOUND));
    }

    @Transactional
    @Override
    public void deleteAnswer(UUID answerId) {

        AnswerEntity answer = getAnswerById(answerId);

        answerRepository.delete(answer);
    }

    @Override
    public AnswerEntity correctAnswer(QuestionEntity question) {

        return answerRepository.findByQuestionAndCorrectAnswer(question, Boolean.TRUE)
                .orElseThrow(
                        () -> new CustomException(ErrorEnum.ANSWER_BY_CORRECT_QUESTION_NOT_FOUND)
                );
    }

    @Override
    public AnswerEntity choiceAnswer(QuestionEntity question, MockTestEntity mockTest) {
        for (DetailMockTestEntity detailMockTest : detailMockTestRepository.findAllByMockTest(mockTest)) {
            if (detailMockTest.getAnswer().getQuestion().equals(question)) {
                return detailMockTest.getAnswer();
            }
        }
        return null;
    }

    @Override
    public List<AnswerEntity> getListAnswerByQuestionId(UUID questionId) {

        QuestionEntity question = questionService.getQuestionById(questionId);

        return question.getAnswers();
    }
}
