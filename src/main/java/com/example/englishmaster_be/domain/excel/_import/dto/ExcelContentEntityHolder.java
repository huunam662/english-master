package com.example.englishmaster_be.domain.excel._import.dto;

import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import lombok.Data;
import java.util.*;

@Data
public class ExcelContentEntityHolder {

    private List<PartEntity> parts;
    private List<QuestionEntity> questionParents;
    private List<QuestionEntity> questionChilds;
    private List<AnswerEntity> answers;
    private Map<UUID, Integer> topicNumberQuestions;

    public ExcelContentEntityHolder() {
        parts = new ArrayList<>();
        questionParents = new ArrayList<>();
        questionChilds = new ArrayList<>();
        answers = new ArrayList<>();
        topicNumberQuestions = new HashMap<>();
    }
}
