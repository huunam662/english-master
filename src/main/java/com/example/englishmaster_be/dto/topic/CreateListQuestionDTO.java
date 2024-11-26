package com.example.englishmaster_be.dto.topic;

import com.example.englishmaster_be.dto.question.CreateQuestionDTO;

import java.util.List;

public class CreateListQuestionDTO {
    private List<CreateQuestionDTO> listQuestion;

    public CreateListQuestionDTO() {
    }

    public List<CreateQuestionDTO> getListQuestion() {
        return listQuestion;
    }

    public void setListQuestion(List<CreateQuestionDTO> listQuestion) {
        this.listQuestion = listQuestion;
    }
}
