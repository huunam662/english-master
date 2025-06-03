package com.example.englishmaster_be.util;

import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.question.QuestionRepository;
import com.example.englishmaster_be.model.topic.TopicEntity;

import java.util.*;
import java.util.stream.Collectors;

public class TopicUtil {

    public static void fillAnswerToTopic(
            TopicEntity topic,
            List<AnswerEntity> answersQuestionChild,
            QuestionRepository questionRepository
    ) {

        Map<QuestionEntity, List<AnswerEntity>> questionChildAnswersGroup = answersQuestionChild.stream()
                .collect(Collectors.groupingBy(AnswerEntity::getQuestion));

        Map<QuestionEntity, List<QuestionEntity>> questionParentChildsGroup = questionChildAnswersGroup.keySet().stream()
                .collect(Collectors.groupingBy(QuestionEntity::getQuestionGroupParent));

        Map<PartEntity, List<QuestionEntity>> partQuestionParentsGroup = questionParentChildsGroup.keySet().stream()
                .collect(Collectors.groupingBy(QuestionEntity::getPart));

        List<UUID> questionParentIds = questionParentChildsGroup.keySet().stream()
                .map(QuestionEntity::getQuestionId).toList();

        List<UUID> questionChildIds = questionChildAnswersGroup.keySet().stream()
                .map(QuestionEntity::getQuestionId).toList();

        List<QuestionEntity> questionParentContents = questionRepository.findContentInQuestionIds(questionParentIds);

        List<QuestionEntity> questionChildContents = questionRepository.findContentInQuestionIds(questionChildIds);

        Map<QuestionEntity, Set<ContentEntity>> questionParentContentsGroup = questionParentContents.stream()
                .collect(Collectors.toMap(
                        question -> question,
                        QuestionEntity::getContentCollection
                ));

        Map<QuestionEntity, Set<ContentEntity>> questionChildContentsGroup = questionChildContents.stream()
                .collect(Collectors.toMap(
                        question -> question,
                        QuestionEntity::getContentCollection
                ));

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
                    questionChild.setContentCollection(questionChildContentsGroup.getOrDefault(questionChild, Collections.emptySet()));
                }
                questionParent.setQuestionGroupChildren(new HashSet<>(questionChilds));
                questionParent.setContentCollection(questionParentContentsGroup.getOrDefault(questionParent, Collections.emptySet()));
            }
            part.setQuestions(new HashSet<>(questionParents));
            part.setTopics(Set.of(topic));
        }

        partsOfTopic = partsOfTopic.stream()
                .sorted(Comparator.comparing(PartEntity::getPartName))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        topic.setParts(partsOfTopic);
    }

}
