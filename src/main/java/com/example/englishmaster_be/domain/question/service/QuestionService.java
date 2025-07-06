package com.example.englishmaster_be.domain.question.service;

import com.example.englishmaster_be.domain.question.dto.projection.INumberAndScoreQuestionTopic;
import com.example.englishmaster_be.domain.question.dto.request.*;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.question.repository.jdbc.QuestionJdbcRepository;
import com.example.englishmaster_be.domain.question.repository.jpa.QuestionRepository;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
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
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public INumberAndScoreQuestionTopic getNumberAndScoreQuestionTopic(UUID topicId) {

        Assert.notNull(topicId, "Id of topic is required.");

        return questionRepository.findNumberAndScoreQuestions(topicId);
    }

    @Transactional
    @Override
    public void orderQuestionNumberToTopicId(UUID topicId) {
        TopicEntity topic = topicService.getTopicById(topicId);
        String topicType = topic.getTopicType().getTopicTypeName();
        List<QuestionEntity> questions;
        if(topicType.equalsIgnoreCase("speaking")){
            questions = questionRepository.findAllQuestionSpeakingOfTopic(topicId);
        }
        else if(topicType.equalsIgnoreCase("writing")){
            questions = questionRepository.findAllQuestionWritingOfTopic(topicId);
        }
        else{
            questions = questionRepository.findAllReadingListeningByTopicId(topicId);
            questions = questions.stream().flatMap(questionParent -> {
                    if(questionParent.getQuestionGroupChildren() == null)
                        return null;
                    questionParent.getQuestionGroupChildren().forEach(
                            questionChild -> questionChild.setPart(questionParent.getPart())
                    );
                    return questionParent.getQuestionGroupChildren().stream();
                }
            ).toList();
        }
        Map<PartEntity, List<QuestionEntity>> partQuestions = questions.stream().collect(
                Collectors.groupingBy(QuestionEntity::getPart)
        );
        List<PartEntity> parts = partQuestions.keySet().stream()
                .sorted(Comparator.comparing(PartEntity::getPartName))
                .toList();
        int questionNumberIncrease = 0;
        List<QuestionEntity> questionToUpdateNumber = new ArrayList<>();
        for(PartEntity part : parts){
            List<QuestionEntity> questionsSort = partQuestions.getOrDefault(part, new ArrayList<>())
                    .stream()
                    .sorted(Comparator.comparing(
                            QuestionEntity::getQuestionNumber,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    )).toList();
            for(QuestionEntity question : questionsSort){
                questionNumberIncrease++;
                Integer questionNumber = question.getQuestionNumber();
                if(questionNumber == null || questionNumber != questionNumberIncrease){
                    question.setQuestionNumber(questionNumberIncrease);
                    questionToUpdateNumber.add(question);
                }
            }
        }
        questionJdbcRepository.batchUpdateQuestionNumber(questionToUpdateNumber);
    }
}
