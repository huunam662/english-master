package com.example.englishmaster_be.domain.exam.question.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.domain.exam.question.dto.req.CreateQuestionParentReq;
import com.example.englishmaster_be.domain.exam.question.dto.req.EditQuestionParentReq;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionPartRes;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.question.util.QuestionUtil;
import com.example.englishmaster_be.domain.exam.question.dto.projection.INumberAndScoreQuestionTopic;
import com.example.englishmaster_be.domain.exam.question.repository.QuestionJdbcRepository;
import com.example.englishmaster_be.domain.exam.question.repository.QuestionRepository;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.service.ITopicService;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.exam.answer.repository.AnswerJdbcRepository;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.exam.answer.repository.AnswerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j(topic = "QUESTION-SERVICE")
@Service
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final IUserService userService;
    private final ITopicService topicService;
    private final QuestionJdbcRepository questionJdbcRepository;
    private final AnswerJdbcRepository answerJdbcRepository;

    @Lazy
    public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository, IUserService userService, ITopicService topicService, QuestionJdbcRepository questionJdbcRepository, AnswerJdbcRepository answerJdbcRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userService = userService;
        this.topicService = topicService;
        this.questionJdbcRepository = questionJdbcRepository;
        this.answerJdbcRepository = answerJdbcRepository;
    }

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
    public List<QuestionPartRes> getAllPartQuestions(String partName, UUID topicId) {

        return topicService.getQuestionOfToTopicPart(topicId, partName);
    }


    @Transactional
    @Override
    public void createListQuestionsParentOfPart(PartEntity part, List<CreateQuestionParentReq> questionParentsRequest) {

        UserEntity userCurrent = userService.currentUser();

        if(part == null)
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Part is null.");

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
    public void editListQuestionsParentOfPart(PartEntity part, List<EditQuestionParentReq> questionParentsRequest) {

        UserEntity userCurrent = userService.currentUser();

        if(part == null)
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Part is null.");

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
        }
        Map<PartEntity, List<QuestionEntity>> partQuestions = questions.stream().collect(
                Collectors.groupingBy(QuestionEntity::getPart)
        );
        List<PartEntity> parts = partQuestions.keySet().stream()
                .sorted(Comparator.comparing(PartEntity::getPartName))
                .toList();
        int questionNumberParentIncrease = 0;
        int questionNumberChildIncrease = 0;
        List<QuestionEntity> questionToUpdateNumber = new ArrayList<>();
        for(PartEntity part : parts){
            List<QuestionEntity> questionsParentSort = partQuestions.getOrDefault(part, new ArrayList<>()).stream()
                    .sorted(Comparator.comparing(QuestionEntity::getQuestionNumber, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
            for(QuestionEntity questionParent : questionsParentSort){
                questionNumberParentIncrease++;
                Integer questionParentNumber = questionParent.getQuestionNumber();
                if(questionParentNumber == null || questionParentNumber != questionNumberParentIncrease){
                    questionParent.setQuestionNumber(questionNumberParentIncrease);
                    questionToUpdateNumber.add(questionParent);
                }
                List<QuestionEntity> questionsChildSort = questionParent.getQuestionGroupChildren().stream()
                        .sorted(Comparator.comparing(QuestionEntity::getQuestionNumber, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList();
                for(QuestionEntity questionChild : questionsChildSort){
                    questionNumberChildIncrease++;
                    Integer questionChildNumber = questionParent.getQuestionNumber();
                    if(questionChildNumber == null || questionChildNumber != questionNumberChildIncrease){
                        questionChild.setQuestionNumber(questionNumberChildIncrease);
                        questionToUpdateNumber.add(questionChild);
                    }
                }
            }
        }
        questionJdbcRepository.batchUpdateQuestionNumber(questionToUpdateNumber);
    }
}
