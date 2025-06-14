package com.example.englishmaster_be.domain.topic.util;

import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;

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

        Set<PartEntity> partsOfTopic = partQuestionParentsGroup.keySet();
        for(PartEntity part: partsOfTopic){
            if(part == null) continue;
            List<QuestionEntity> questionParents = partQuestionParentsGroup.getOrDefault(part, Collections.emptyList());
            for(QuestionEntity questionParent: questionParents){
                if(questionParent == null) continue;
                List<QuestionEntity> questionChilds = questionParentChildsGroup.getOrDefault(questionParent, Collections.emptyList());
                for(QuestionEntity questionChild: questionChilds){
                    if(questionChild == null) continue;
                    List<AnswerEntity> answersChild = questionChildAnswersGroup.getOrDefault(questionChild, Collections.emptyList());
                    questionChild.setAnswers(new HashSet<>(answersChild));
                }
                questionParent.setQuestionGroupChildren(new HashSet<>(questionChilds));
            }
            part.setQuestions(new HashSet<>(questionParents));
            part.setTopic(topic);
        }

        partsOfTopic = partsOfTopic.stream()
                .sorted(Comparator.comparing(PartEntity::getPartName))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        topic.setParts(partsOfTopic);
    }

    public static void fillQuestionSpeakingToTopic(
            TopicEntity topic,
            List<QuestionEntity> questionSpeakings
    ) {

        Map<PartEntity, List<QuestionEntity>> partQuestionParentsGroup = questionSpeakings.stream()
                .collect(Collectors.groupingBy(QuestionEntity::getPart));

        Set<PartEntity> partsOfTopic = partQuestionParentsGroup.keySet();
        for(PartEntity part: partsOfTopic){
            if(part == null) continue;
            List<QuestionEntity> questions = partQuestionParentsGroup.getOrDefault(part, Collections.emptyList());
            part.setQuestions(new HashSet<>(questions));
            part.setTopic(topic);
        }

        partsOfTopic = partsOfTopic.stream()
                .sorted(Comparator.comparing(PartEntity::getPartName))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        topic.setParts(partsOfTopic);
    }
}
