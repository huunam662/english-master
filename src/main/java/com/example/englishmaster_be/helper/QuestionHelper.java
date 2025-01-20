package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionHelper {

    public static List<QuestionEntity> shuffleQuestionsAndAnswers(List<QuestionEntity> questionParentsList, PartEntity partEntity) {

        if(questionParentsList == null || partEntity == null) return null;

        questionParentsList = new ArrayList<>(questionParentsList);

        Collections.shuffle(questionParentsList);

        List<String> partTypesNotShuffle = List.of("Picture Description", "Question - Response", "Words Fill Completion", "Words Matching");

        boolean notShuffleAnswer = partTypesNotShuffle.stream().anyMatch(
                partType -> partType.equalsIgnoreCase(partEntity.getPartType())
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

    public static int calculateTotalQuestionOf(List<QuestionEntity> questionParents) {

        if(questionParents == null) return 0;

        int totalQuestion = questionParents.size();

        int totalQuestionChild = questionParents.stream().map(
                questionEntity -> {
                    if(questionEntity.getQuestionGroupChildren() == null) return 0;

                    return questionEntity.getQuestionGroupChildren().size();
                }
        ).reduce(0, Integer::sum);

        return totalQuestion + totalQuestionChild;
    }

}
