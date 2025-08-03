package com.example.englishmaster_be.domain.exam.question.util;

import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.domain.exam.answer.dto.req.CreateAnswerReq;
import com.example.englishmaster_be.domain.exam.answer.dto.req.EditAnswerReq;
import com.example.englishmaster_be.domain.exam.question.dto.req.CreateQuestionChildReq;
import com.example.englishmaster_be.domain.exam.question.dto.req.CreateQuestionParentReq;
import com.example.englishmaster_be.domain.exam.question.dto.req.EditQuestionChildReq;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionPartRes;
import com.example.englishmaster_be.domain.exam.question.mapper.QuestionMapper;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.question.dto.req.EditQuestionParentReq;
import com.example.englishmaster_be.domain.exam.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionRes;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import java.util.*;

public class QuestionUtil {


<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/exam/question/util/QuestionUtil.java
=======
        if(questionEntityList == null) return null;

        if(!partEntity.getTopic().getTopicType().getTopicTypeName().equalsIgnoreCase("speaking"))
            questionEntityList = shuffleQuestionsAndAnswers(questionEntityList, partEntity);

        return questionEntityList.stream().map(
                questionEntity -> {

                    QuestionResponse questionResponse;

                    Boolean isQuestionParent = questionEntity.getIsQuestionParent();

                    if(isQuestionParent) {

                        questionResponse = QuestionMapper.INSTANCE.toQuestionResponse(questionEntity);
                        questionResponse.setNumberOfQuestionsChild(questionEntity.getQuestionGroupChildren() != null ? questionEntity.getQuestionGroupChildren().size() : 0);
                    }
                    else {

                        questionResponse = QuestionMapper.INSTANCE.toQuestionChildResponse(questionEntity);
                        questionResponse.setNumberOfQuestionsChild(0);
                    }

                    return questionResponse;
                }
        ).toList();
    }


    public static List<QuestionEntity> shuffleQuestionsAndAnswers(Collection<QuestionEntity> questionParentsList, PartEntity partEntity) {

        if(questionParentsList == null || partEntity == null) return null;

        List<QuestionEntity> questionParentsShuffle = new ArrayList<>(questionParentsList);

        Collections.shuffle(questionParentsShuffle);

        List<PartType> partTypesNotShuffle = List.of(
                PartType.PART_1_TOEIC,
                PartType.PART_2_TOEIC,
                PartType.PART_1_IELTS,
                PartType.PART_2_IELTS
        );

        boolean notShuffleAnswer = partTypesNotShuffle.stream().anyMatch(
                partType -> partType.getType().equalsIgnoreCase(partEntity.getPartType())
        );

        boolean partTypeIsTextCompletion = partEntity.getPartType().equalsIgnoreCase("Text Completion");

        questionParentsShuffle.forEach(questionEntity -> {

            if(questionEntity.getIsQuestionParent() && questionEntity.getQuestionGroupChildren() != null) {

                List<QuestionEntity> questionsList4Shuffle = new ArrayList<>(questionEntity.getQuestionGroupChildren());

                if(!partTypeIsTextCompletion)
                    Collections.shuffle(questionsList4Shuffle);

                questionsList4Shuffle.forEach(
                        questionGroupChildEntity -> {

                            if(notShuffleAnswer){
                                questionGroupChildEntity.setAnswers(
                                        questionGroupChildEntity.getAnswers().stream().sorted(
                                                Comparator.comparing(AnswerEntity::getAnswerContent)
                                        ).collect(Collectors.toCollection(LinkedHashSet::new))
                                );
                            }
                            else{
                                if(questionGroupChildEntity.getAnswers() != null){

                                    List<AnswerEntity> answersList4Shuffle = new ArrayList<>(questionGroupChildEntity.getAnswers());

                                    Collections.shuffle(answersList4Shuffle);

                                    questionGroupChildEntity.setAnswers(new HashSet<>(answersList4Shuffle));
                                }
                            }

                        }
                );

                questionEntity.setQuestionGroupChildren(new HashSet<>(questionsList4Shuffle));
            }

        });

        return new ArrayList<>(questionParentsShuffle);
    }
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/question/util/QuestionUtil.java

    public static int totalQuestionChildOf(Collection<QuestionEntity> questionParents) {

        if(questionParents == null) return 0;

        return questionParents.stream()
                .filter(Objects::nonNull)
                .map(
                questionEntity -> questionEntity.getQuestionGroupChildren().size()
        ).reduce(0, Integer::sum);
    }

    public static int totalQuestionChildOf(Collection<PartEntity> partEntityList, TopicEntity topicEntity){

        if(partEntityList == null || topicEntity == null) return 0;

        return partEntityList.stream().map(
                    PartEntity::getQuestions
                )
                .filter(Objects::nonNull)
                .map(
                        QuestionUtil::totalQuestionChildOf
                ).reduce(0, Integer::sum);
    }

    public static void fillToCreateQuestionAnswerForPart(
            List<CreateQuestionParentReq> questionParentsRequest,
            PartEntity part,
            UserEntity userCurrent,
            List<QuestionEntity> questionParentToSave,
            List<QuestionEntity> questionChildToSave,
            List<AnswerEntity> answerChildToSave
    ){
        if(questionParentsRequest == null) return;

        for(CreateQuestionParentReq parentRequest : questionParentsRequest){

            if(parentRequest == null) continue;

            QuestionEntity questionParent = QuestionMapper.INSTANCE.toQuestionParent(parentRequest);
            questionParent.setQuestionId(UUID.randomUUID());
            questionParent.setPart(part);
            questionParent.setIsQuestionParent(true);
            questionParent.setUserCreate(userCurrent);
            questionParent.setUserUpdate(userCurrent);
            questionParent.setQuestionType(QuestionType.Question_Parent);
            questionParent.setQuestionScore(0);
            questionParent.setQuestionGroupChildren(new LinkedHashSet<>());

            questionParentToSave.add(questionParent);

            if(parentRequest.getQuestionChilds() == null || parentRequest.getQuestionChilds().isEmpty())
                continue;

            List<CreateQuestionChildReq> childRequestList = parentRequest.getQuestionChilds();

            for(CreateQuestionChildReq childRequest : childRequestList){

                if(childRequest == null) continue;

                QuestionEntity questionChild = QuestionMapper.INSTANCE.toQuestionChild(childRequest);
                questionChild.setQuestionId(UUID.randomUUID());
                questionChild.setUserCreate(userCurrent);
                questionChild.setUserUpdate(userCurrent);
                questionChild.setPart(part);
                questionChild.setQuestionGroupParent(questionParent);
                questionChild.setIsQuestionParent(false);
                questionChild.setQuestionType(QuestionType.Question_Child);
                questionChild.setQuestionScore(childRequest.getQuestionScore());
                questionChild.setAnswers(new LinkedHashSet<>());

                questionParent.setQuestionScore(questionParent.getQuestionScore() + childRequest.getQuestionScore());
                questionParent.getQuestionGroupChildren().add(questionChild);
                questionChildToSave.add(questionChild);

                if(childRequest.getAnswers() == null || childRequest.getAnswers().isEmpty())
                    continue;

                List<CreateAnswerReq> answerChildList = childRequest.getAnswers();

                for(CreateAnswerReq answer1Request : answerChildList){

                    if(answer1Request == null) continue;

                    AnswerEntity answer = AnswerMapper.INSTANCE.toAnswerEntity(answer1Request);
                    answer.setAnswerId(UUID.randomUUID());
                    answer.setUserCreate(userCurrent);
                    answer.setUserUpdate(userCurrent);
                    answer.setQuestion(questionChild);

                    questionChild.getAnswers().add(answer);
                    answerChildToSave.add(answer);
                }
            }
        }
    }

    public static void fillToUpdateQuestionAnswerForPart(
            List<EditQuestionParentReq> questionParentsRequest,
            PartEntity part,
            UserEntity userCurrent,
            List<QuestionEntity> questionParentToCreate,
            List<QuestionEntity> questionParentToUpdate,
            List<QuestionEntity> questionChildToCreate,
            List<QuestionEntity> questionChildToUpdate,
            List<AnswerEntity> answerChildToCreate,
            List<AnswerEntity> answerChildToUpdate
    ){

        if(questionParentsRequest == null) return;

        for(EditQuestionParentReq questionParentRequest : questionParentsRequest){
            if(questionParentRequest == null) continue;

            QuestionEntity questionParent = QuestionMapper.INSTANCE.toQuestionEntity(questionParentRequest);
            questionParent.setIsQuestionParent(true);
            questionParent.setQuestionType(QuestionType.Question_Parent);
            questionParent.setQuestionScore(0);
            questionParent.setPart(part);
            questionParent.setQuestionGroupChildren(new LinkedHashSet<>());

            if(questionParentRequest.getQuestionParentId() == null){
                questionParent.setQuestionId(UUID.randomUUID());
                questionParent.setUserCreate(userCurrent);
                questionParentToCreate.add(questionParent);
            }
            else{
                questionParent.setQuestionId(questionParentRequest.getQuestionParentId());
                questionParent.setUserUpdate(userCurrent);
                questionParentToUpdate.add(questionParent);
            }

            List<EditQuestionChildReq> questionChilds = questionParentRequest.getQuestionChilds();
            if(questionChilds == null || questionChilds.isEmpty()) continue;

            for(EditQuestionChildReq questionChildRequest : questionChilds){
                if(questionChildRequest == null) continue;

                QuestionEntity questionChild = QuestionMapper.INSTANCE.toQuestionEntity(questionChildRequest);
                questionChild.setIsQuestionParent(false);
                questionChild.setQuestionType(QuestionType.Question_Child);
                questionChild.setPart(part);
                questionChild.setQuestionGroupParent(questionParent);
                questionChild.setAnswers(new LinkedHashSet<>());
                questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());

                if(questionChildRequest.getQuestionChildId() == null){
                    questionChild.setQuestionId(UUID.randomUUID());
                    questionChild.setUserCreate(userCurrent);
                    questionChildToCreate.add(questionChild);
                }
                else{
                    questionChild.setQuestionId(questionChildRequest.getQuestionChildId());
                    questionChild.setUserUpdate(userCurrent);
                    questionChildToUpdate.add(questionChild);
                }

                List<EditAnswerReq> answersChild = questionChildRequest.getAnswers();
                if(answersChild == null || answersChild.isEmpty()) continue;

                for (EditAnswerReq answerChildRequest : answersChild){
                    if(answerChildRequest == null) continue;

                    AnswerEntity answer = AnswerMapper.INSTANCE.toAnswerEntity(answerChildRequest);
                    answer.setQuestion(questionChild);

                    if(answer.getAnswerId() == null){
                        answer.setAnswerId(UUID.randomUUID());
                        answer.setUserCreate(userCurrent);
                        answerChildToCreate.add(answer);
                    }
                    else{
                        answer.setAnswerId(answerChildRequest.getAnswerId());
                        answer.setUserUpdate(userCurrent);
                        answerChildToUpdate.add(answer);
                    }

                    questionChild.getAnswers().add(answer);
                }

                questionParent.getQuestionGroupChildren().add(questionChild);
            }
        }
    }



}
