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

        List<PartType> partTypesNotShuffle = List.of(PartType.PART_1_TOEIC, PartType.PART_2_TOEIC);

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

                            if (questionGroupChildEntity.getQuestionGroupChildren() != null)
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

}
