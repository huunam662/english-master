package com.example.englishmaster_be.domain.topic.util;

import com.example.englishmaster_be.common.constant.TopicType;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import io.github.perplexhub.rsql.Q;

import java.util.*;
import java.util.stream.Collectors;

public class TopicUtil {

    public static void fillAnswerToTopic(
            TopicEntity topic,
            List<AnswerEntity> answersQuestionChild
    ) {
        Map<QuestionEntity, List<AnswerEntity>> questionChildAnswersGroup = answersQuestionChild.stream()
                .collect(Collectors.groupingBy(AnswerEntity::getQuestion));
        Map<QuestionEntity, List<QuestionEntity>> questionParentChildsGroup = questionChildAnswersGroup.keySet().stream()
                .collect(Collectors.groupingBy(QuestionEntity::getQuestionGroupParent));
        Map<PartEntity, List<QuestionEntity>> partQuestionParentsGroup = questionParentChildsGroup.keySet().stream()
                .collect(Collectors.groupingBy(QuestionEntity::getPart));
        Set<PartEntity> partsOfTopic = partQuestionParentsGroup.keySet().stream()
                .sorted(Comparator.comparing(PartEntity::getPartName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        for(PartEntity part: partsOfTopic){
            if(part == null) continue;
            List<QuestionEntity> questionParents = partQuestionParentsGroup.getOrDefault(part, Collections.emptyList()).stream()
                    .sorted(Comparator.comparing(QuestionEntity::getQuestionNumber, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
            for(QuestionEntity questionParent: questionParents){
                if(questionParent == null) continue;
                List<QuestionEntity> questionChilds = questionParentChildsGroup.getOrDefault(questionParent, Collections.emptyList()).stream()
                        .sorted(Comparator.comparing(QuestionEntity::getQuestionNumber, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList();
                for(QuestionEntity questionChild: questionChilds){
                    if(questionChild == null) continue;
                    String questionParentContent = questionParent.getQuestionContent();
                    if(questionParentContent != null && questionParentContent.contains("_()")){
                        questionParent.setQuestionContent(questionParentContent.replaceFirst("\\(\\)", String.format("<strong><i>(%d)</i></strong>", questionChild.getQuestionNumber())));
                    }
                    List<AnswerEntity> answersChild = questionChildAnswersGroup.getOrDefault(questionChild, Collections.emptyList());
                    questionChild.setAnswers(new LinkedHashSet<>(answersChild));
                }
                questionParent.setQuestionGroupChildren(new LinkedHashSet<>(questionChilds));
            }
            part.setQuestions(new LinkedHashSet<>(questionParents));
            part.setTopic(topic);
        }
        topic.setParts(partsOfTopic);
    }

    public static void fillQuestionSpeakingOrWritingToTopic(
            TopicEntity topic,
            List<QuestionEntity> questionSpeakings
    ) {
        List<QuestionEntity> questionSpeakingsSort = questionSpeakings.stream()
                .sorted(Comparator.comparing(QuestionEntity::getQuestionNumber, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        Map<PartEntity, List<QuestionEntity>> partQuestionParentsGroup = questionSpeakingsSort.stream()
                .collect(Collectors.groupingBy(QuestionEntity::getPart));
        Set<PartEntity> partsOfTopic = partQuestionParentsGroup.keySet();
        for(PartEntity part: partsOfTopic){
            if(part == null) continue;
            List<QuestionEntity> questions = partQuestionParentsGroup.getOrDefault(part, Collections.emptyList());
            part.setQuestions(new LinkedHashSet<>(questions));
            part.setTopic(topic);
        }
        topic.setParts(partsOfTopic);
    }
}
