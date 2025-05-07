package com.example.englishmaster_be.util;

import com.example.englishmaster_be.common.constant.PartType;
import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.mapper.QuestionMapper;
import com.example.englishmaster_be.domain.question.dto.response.QuestionMatchingResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.mapper.QuestionMapper;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class QuestionUtil {

    public static List<QuestionPartResponse> parseQuestionPartResponseList(List<QuestionEntity> questionEntityList, List<PartEntity> partEntityList, TopicEntity topicEntity, Boolean isAdmin){

        if(questionEntityList == null) return null;

        return partEntityList.stream().map(
                partEntity -> {

                    List<QuestionEntity> questionEntityListFilter = questionEntityList.stream().filter(
                            questionEntity -> questionEntity.getPart().equals(partEntity)
                                    && questionEntity.getTopics().contains(topicEntity)
                    ).toList();

                    QuestionPartResponse questionPartResponse = QuestionMapper.INSTANCE.toQuestionPartResponse(questionEntityListFilter, partEntity, topicEntity, isAdmin);

                    questionEntityList.removeAll(questionEntityListFilter);

                    return questionPartResponse;

                }
        ).toList();
    }

    public static List<QuestionResponse> parseQuestionResponseList(List<QuestionEntity> questionEntityList, TopicEntity topicEntity, PartEntity partEntity, Boolean isAdmin){

        if(questionEntityList == null) return null;

        questionEntityList = shuffleQuestionsAndAnswers(questionEntityList, partEntity);

        return questionEntityList.stream().map(
                questionEntity -> {

                    QuestionResponse questionResponse = QuestionMapper.INSTANCE.toQuestionResponse(questionEntity, topicEntity, partEntity, isAdmin);

                    if(questionResponse == null) return null;

                    questionResponse.setNumberOfQuestionsChild(questionEntity.getQuestionGroupChildren() != null ? questionEntity.getQuestionGroupChildren().size() : 0);

                    return questionResponse;
                }
        ).toList();
    }


    public static List<QuestionEntity> shuffleQuestionsAndAnswers(List<QuestionEntity> questionParentsList, PartEntity partEntity) {

        if(questionParentsList == null || partEntity == null) return null;

        questionParentsList = new ArrayList<>(questionParentsList);

        Collections.shuffle(questionParentsList);

        List<PartType> partTypesNotShuffle = List.of(PartType.PART_1_TOEIC, PartType.PART_2_TOEIC);

        boolean notShuffleAnswer = partTypesNotShuffle.stream().anyMatch(
                partType -> partType.getType().equalsIgnoreCase(partEntity.getPartType())
        );

        boolean partTypeIsTextCompletion = partEntity.getPartType().equalsIgnoreCase("Text Completion");

        questionParentsList.forEach(questionEntity -> {

            if(questionEntity.getQuestionGroupChildren() != null) {

                List<QuestionEntity> questionsList4Shuffle = new ArrayList<>(questionEntity.getQuestionGroupChildren());

                if(!partTypeIsTextCompletion)
                    Collections.shuffle(questionsList4Shuffle);

                questionsList4Shuffle.forEach(
                        questionGroupChildEntity -> {

                            if (questionGroupChildEntity.getQuestionGroupChildren() != null)
                                questionGroupChildEntity.setQuestionGroupChildren(null);

                            if(!notShuffleAnswer){

                                if(questionGroupChildEntity.getAnswers() != null){

                                    List<AnswerEntity> answersList4Shuffle = new ArrayList<>(questionGroupChildEntity.getAnswers());

                                    Collections.shuffle(answersList4Shuffle);

                                    questionGroupChildEntity.setAnswers(answersList4Shuffle);
                                }
                            }

                        }
                );

                questionEntity.setQuestionGroupChildren(questionsList4Shuffle);
            }

        });

        return questionParentsList;
    }

    public static int totalQuestionChildOf(List<QuestionEntity> questionParents) {

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

    public static int totalQuestionChildOf(List<PartEntity> partEntityList, TopicEntity topicEntity){

        if(partEntityList == null || topicEntity == null) return 0;

        return partEntityList.stream().map(
                    PartEntity::getQuestions
                )
                .filter(Objects::nonNull)
                .map(
                        QuestionUtil::totalQuestionChildOf
                ).reduce(0, Integer::sum);
    }

    public static int totalScoreQuestionsParent(List<QuestionEntity> questionEntityList){

        if(questionEntityList == null) return 0;

        return questionEntityList.stream()
                .filter(Objects::nonNull)
                .filter(QuestionEntity::getIsQuestionParent)
                .map(QuestionEntity::getQuestionScore)
                .reduce(0, Integer::sum);
    }

}
