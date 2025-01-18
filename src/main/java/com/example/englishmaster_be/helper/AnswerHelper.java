package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.model.answer.AnswerEntity;

import java.util.List;
import java.util.UUID;

public class AnswerHelper {

    public static UUID getIdCorrectAnswer(List<AnswerEntity> answers) {

        if(answers == null) return null;

        return answers.stream()
                .filter(
                answerEntity -> answerEntity.getCorrectAnswer().equals(Boolean.TRUE)
                )
                .map(AnswerEntity::getAnswerId)
                .findFirst()
                .orElse(null);

    }
}
