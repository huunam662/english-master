package com.example.englishmaster_be.domain.exam.question.mapper;

import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionPartRes;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionRes;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.question.util.QuestionUtil;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;

import java.util.*;

public class QuestionMapperUtil {

    public static List<QuestionRes> mapToQuestionResponseList(Collection<QuestionEntity> questionEntityList){

        if(questionEntityList == null) return null;

        return questionEntityList.stream().map(
                questionEntity -> {
                    QuestionRes questionResponse;
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

    public static List<QuestionPartRes> mapToQuestionPartResList(TopicEntity topic){
        if (topic == null) return Collections.emptyList();

        if(topic.getParts() == null || topic.getParts().isEmpty())
            return Collections.emptyList();

        return topic.getParts().stream()
                .sorted(Comparator.comparing(PartEntity::getPartName, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(part -> {
                            String topicType = topic.getTopicType().getTopicTypeName();
                            boolean isSpeakingOrWriting = topicType.equalsIgnoreCase("speaking") || topicType.equalsIgnoreCase("writing");
                            int totalQuestionOfPart;
                            if(isSpeakingOrWriting) {
                                part.getQuestions().forEach(questionParent -> questionParent.setQuestionGroupChildren(new LinkedHashSet<>()));
                                totalQuestionOfPart = part.getQuestions().size();
                            }
                            else {
                                totalQuestionOfPart = QuestionUtil.totalQuestionChildOf(part.getQuestions());
                            }
                            QuestionPartRes questionPartResponse = QuestionMapper.INSTANCE.toQuestionPartResponse(part.getQuestions(), topic, part);
                            questionPartResponse.getPart().setTotalQuestion(totalQuestionOfPart);
                            return questionPartResponse;
                        }
                ).toList();
    }
}
