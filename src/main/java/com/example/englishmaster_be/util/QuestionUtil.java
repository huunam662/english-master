package com.example.englishmaster_be.util;

import com.example.englishmaster_be.common.constant.PartType;
import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.domain.answer.dto.request.CreateAnswer1Request;
import com.example.englishmaster_be.domain.question.dto.request.CreateQuestionChildRequest;
import com.example.englishmaster_be.domain.question.dto.request.CreateQuestionParentRequest;
import com.example.englishmaster_be.mapper.AnswerMapper;
import com.example.englishmaster_be.mapper.QuestionMapper;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;

import java.util.*;

public class QuestionUtil {

    public static List<QuestionResponse> parseQuestionResponseList(Collection<QuestionEntity> questionEntityList, PartEntity partEntity){

        if(questionEntityList == null) return null;

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

        boolean partTypeIsTextCompletion = partEntity.getPartType().equalsIgnoreCase(PartType.PART_6_TOEIC.getType());

        questionParentsShuffle.forEach(questionEntity -> {

            if(questionEntity.getIsQuestionParent() && questionEntity.getQuestionGroupChildren() != null) {

                List<QuestionEntity> questionsList4Shuffle = new ArrayList<>(questionEntity.getQuestionGroupChildren());

                if(!partTypeIsTextCompletion)
                    Collections.shuffle(questionsList4Shuffle);

                questionsList4Shuffle.forEach(
                        questionGroupChildEntity -> {

                            if (!questionGroupChildEntity.getIsQuestionParent() && questionGroupChildEntity.getQuestionGroupChildren() != null)
                                questionGroupChildEntity.setQuestionGroupChildren(null);

                            if(!notShuffleAnswer && !questionGroupChildEntity.getIsQuestionParent()){

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

    public static int totalQuestionChildOf(Collection<QuestionEntity> questionParents) {

        if(questionParents == null) return 0;

        return questionParents.stream()
                .filter(Objects::nonNull)
                .map(
                questionEntity -> {
                    if(questionEntity.getQuestionType().equals(QuestionType.Words_Matching))
                        return 1;

                    return questionEntity.getQuestionGroupChildren().size();
                }
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

    public static int totalScoreQuestionsParent(Collection<QuestionEntity> questionEntityList){

        if(questionEntityList == null) return 0;

        return questionEntityList.stream()
                .filter(Objects::nonNull)
                .filter(QuestionEntity::getIsQuestionParent)
                .map(QuestionEntity::getQuestionScore)
                .reduce(0, Integer::sum);
    }

    public static void fillToCreateQuestionAnswerForPart(
            List<CreateQuestionParentRequest> questionParentsRequest,
            PartEntity part,
            UserEntity userCurrent,
            List<ContentEntity> contentToSave,
            List<QuestionEntity> questionParentToSave,
            List<QuestionEntity> questionChildToSave,
            List<AnswerEntity> answerChildToSave
    ){

        for(CreateQuestionParentRequest parentRequest : questionParentsRequest){

            if(parentRequest == null) continue;

            QuestionEntity questionParent = QuestionMapper.INSTANCE.toQuestionParent(parentRequest);
            questionParent.setQuestionId(UUID.randomUUID());
            questionParent.setPart(part);
            questionParent.setIsQuestionParent(true);
            questionParent.setUserCreate(userCurrent);
            questionParent.setUserUpdate(userCurrent);
            questionParent.setQuestionType(QuestionType.Question_Parent);
            questionParent.setQuestionScore(0);
            questionParent.setQuestionGroupChildren(new HashSet<>());

            Set<ContentEntity> contentPs = new HashSet<>();

            if(parentRequest.getContentAudio() != null && !parentRequest.getContentAudio().isEmpty()){
                ContentEntity contentAudio = ContentEntity.builder()
                        .contentId(UUID.randomUUID())
                        .contentData(parentRequest.getContentAudio())
                        .userCreate(userCurrent)
                        .userUpdate(userCurrent)
                        .build();
                contentPs.add(contentAudio);
                contentToSave.add(contentAudio);
            }
            if(parentRequest.getContentImage() != null && !parentRequest.getContentImage().isEmpty()){
                ContentEntity contentImage = ContentEntity.builder()
                        .contentId(UUID.randomUUID())
                        .contentData(parentRequest.getContentImage())
                        .userCreate(userCurrent)
                        .userUpdate(userCurrent)
                        .build();
                contentPs.add(contentImage);
                contentToSave.add(contentImage);
            }
            if(!contentPs.isEmpty())
                questionParent.setContentCollection(contentPs);

            questionParentToSave.add(questionParent);

            if(parentRequest.getQuestionChilds() == null || parentRequest.getQuestionChilds().isEmpty())
                continue;

            List<CreateQuestionChildRequest> childRequestList = parentRequest.getQuestionChilds();

            for(CreateQuestionChildRequest childRequest : childRequestList){

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
                questionChild.setAnswers(new HashSet<>());
                questionParent.setQuestionScore(questionParent.getQuestionScore() + childRequest.getQuestionScore());

                Set<ContentEntity> contentCs = new HashSet<>();

                if(childRequest.getContentAudio() != null && !childRequest.getContentAudio().isEmpty()){
                    ContentEntity contentAudio = ContentEntity.builder()
                            .contentId(UUID.randomUUID())
                            .contentData(parentRequest.getContentAudio())
                            .userCreate(userCurrent)
                            .userUpdate(userCurrent)
                            .build();
                    contentCs.add(contentAudio);
                    contentToSave.add(contentAudio);
                }
                if(parentRequest.getContentImage() != null && !parentRequest.getContentImage().isEmpty()){
                    ContentEntity contentImage = ContentEntity.builder()
                            .contentId(UUID.randomUUID())
                            .contentData(parentRequest.getContentImage())
                            .userCreate(userCurrent)
                            .userUpdate(userCurrent)
                            .build();
                    contentCs.add(contentImage);
                    contentToSave.add(contentImage);
                }
                if(!contentCs.isEmpty())
                    questionChild.setContentCollection(contentCs);

                questionParent.getQuestionGroupChildren().add(questionChild);
                questionChildToSave.add(questionChild);

                if(childRequest.getAnswers() == null || childRequest.getAnswers().isEmpty())
                    continue;

                List<CreateAnswer1Request> answerChildList = childRequest.getAnswers();

                for(CreateAnswer1Request answer1Request : answerChildList){

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

}
