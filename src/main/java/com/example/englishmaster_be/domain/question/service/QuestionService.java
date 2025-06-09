package com.example.englishmaster_be.domain.question.service;

import com.example.englishmaster_be.domain.question.dto.request.*;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.question.repository.jdbc.QuestionJdbcRepository;
import com.example.englishmaster_be.domain.question.repository.jpa.QuestionRepository;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.question.dto.response.*;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.answer.repository.jdbc.AnswerJdbcRepository;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.answer.repository.jpa.AnswerRepository;
import com.example.englishmaster_be.domain.question.util.QuestionUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Slf4j(topic = "QUESTION-SERVICE")
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionService implements IQuestionService {

    QuestionRepository questionRepository;

    AnswerRepository answerRepository;

    IUserService userService;

    ITopicService topicService;

    QuestionJdbcRepository questionJdbcRepository;

    AnswerJdbcRepository answerJdbcRepository;


    @Override
    public QuestionEntity getQuestionById(UUID questionId) {
        return questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new IllegalArgumentException("QuestionEntity not found with ID: " + questionId)
                );
    }


    @Override
    public int countQuestionToQuestionGroup(QuestionEntity question) {

        if (question == null) return 0;

        return listQuestionGroup(question).size();
    }

    @Override
    public boolean checkQuestionGroup(UUID questionId) {

        QuestionEntity question = getQuestionById(questionId);

        return questionRepository.existsByQuestionGroupParent(question);
    }


    @Override
    public List<QuestionEntity> listQuestionGroup(QuestionEntity question) {
        return questionRepository.findAllByQuestionGroupParent(question);
    }


    @Override
    public List<QuestionPartResponse> getAllPartQuestions(String partName, UUID topicId) {

        return topicService.getQuestionOfToTopicPart(topicId, partName);
    }


    @Transactional
    @Override
    public void createListQuestionsParentOfPart(PartEntity part, List<CreateQuestionParentRequest> questionParentsRequest) {

        UserEntity userCurrent = userService.currentUser();

        if(part == null)
            throw new ErrorHolder(Error.BAD_REQUEST, "Part is null.");

        if(questionParentsRequest == null || questionParentsRequest.isEmpty())
            return;

        List<QuestionEntity> questionParentToSave = new ArrayList<>();

        List<QuestionEntity> questionChildToSave = new ArrayList<>();

        List<AnswerEntity> answerChildToSave = new ArrayList<>();

        QuestionUtil.fillToCreateQuestionAnswerForPart(
                questionParentsRequest,
                part,
                userCurrent,
                questionParentToSave,
                questionChildToSave,
                answerChildToSave
        );

        questionJdbcRepository.batchInsertQuestion(questionParentToSave);
        questionJdbcRepository.batchInsertQuestion(questionChildToSave);
        answerJdbcRepository.batchInsertAnswer(answerChildToSave);
    }

    @Override
    public void editListQuestionsParentOfPart(PartEntity part, List<EditQuestionParentRequest> questionParentsRequest) {

        UserEntity userCurrent = userService.currentUser();

        if(part == null)
            throw new ErrorHolder(Error.BAD_REQUEST, "Part is null.");

        if(questionParentsRequest == null || questionParentsRequest.isEmpty())
            return;

        List<QuestionEntity> questionParentToCreate = new ArrayList<>();

        List<QuestionEntity> questionChildToCreate = new ArrayList<>();

        List<AnswerEntity> answerChildToCreate = new ArrayList<>();

        List<QuestionEntity> questionParentToUpdate = new ArrayList<>();

        List<QuestionEntity> questionChildToUpdate = new ArrayList<>();

        List<AnswerEntity> answerChildToUpdate = new ArrayList<>();

        QuestionUtil.fillToUpdateQuestionAnswerForPart(
                questionParentsRequest,
                part,
                userCurrent,
                questionParentToCreate,
                questionParentToUpdate,
                questionChildToCreate,
                questionChildToUpdate,
                answerChildToCreate,
                answerChildToUpdate
        );

        questionJdbcRepository.batchInsertQuestion(questionParentToCreate);
        questionJdbcRepository.batchUpdateQuestion(questionParentToUpdate);
        questionJdbcRepository.batchInsertQuestion(questionChildToCreate);
        questionJdbcRepository.batchUpdateQuestion(questionChildToUpdate);
        answerJdbcRepository.batchInsertAnswer(answerChildToCreate);
        answerJdbcRepository.batchUpdateAnswer(answerChildToUpdate);
    }

    @Transactional
    @Override
    public void deleteAllQuestions(List<UUID> questionIds) {

        if(questionIds == null || questionIds.isEmpty())
            return;

        List<UUID> answerIds = answerRepository.findAllAnswerIdsIn(questionIds);
        answerRepository.deleteAll(answerIds);
        questionRepository.deleteAll(questionIds);
    }
}
