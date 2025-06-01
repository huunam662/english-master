package com.example.englishmaster_be.domain.answer.service;

import com.example.englishmaster_be.domain.answer.dto.request.AnswerRequest;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.mapper.AnswerMapper;
import com.example.englishmaster_be.model.answer.AnswerRepository;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.mock_test_detail.MockTestDetailRepository;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.englishmaster_be.common.constant.error.Error;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerService implements IAnswerService {

    AnswerRepository answerRepository;

    MockTestDetailRepository detailMockTestRepository;

    IUserService userService;

    IQuestionService questionService;


    @Transactional
    @Override
    public AnswerEntity saveAnswer(AnswerRequest answerRequest) {

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(answerRequest.getQuestionId());

//        boolean questionHadCorrectAnswer = question.getAnswers().stream().anyMatch(
//                answer -> {
//                    if(
//                            Objects.nonNull(answerRequest.getAnswerId())
//                            && answer.getCorrectAnswer()
//                            && answerRequest.getCorrectAnswer()
//                            && !answer.getAnswerId().equals(answerRequest.getAnswerId())
//                    ) return true;
//
//                    else return answer.getCorrectAnswer() && answerRequest.getCorrectAnswer();
//                }
//        );
//
//        if (questionHadCorrectAnswer) throw new BadRequestException("Had correct AnswerEntity");

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
                .orElseThrow(() -> new ErrorHolder(Error.ANSWER_NOT_FOUND));
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
                        () -> new ErrorHolder(Error.ANSWER_BY_CORRECT_QUESTION_NOT_FOUND)
                );
    }

    @Override
    public AnswerEntity choiceAnswer(QuestionEntity question, MockTestEntity mockTest) {
//        for (MockTestDetailEntity detailMockTest : detailMockTestRepository.findAllByMockTest(mockTest)) {
//            if (detailMockTest.getAnswer().getQuestion().equals(question)) {
//                return detailMockTest.getAnswer();
//            }
//        }
        return null;
    }

    @Override
    public List<AnswerEntity> getListAnswerByQuestionId(UUID questionId) {

        QuestionEntity question = questionService.getQuestionById(questionId);

        return question.getAnswers().stream().toList();
    }

    @Transactional
    @Override
    public AnswerEntity save(AnswerEntity answer) {

        return answerRepository.save(answer);
    }

    @Transactional
    @Override
    public List<AnswerEntity> saveAll(Set<AnswerEntity> answers) {
        return answerRepository.saveAll(answers);
    }
}
