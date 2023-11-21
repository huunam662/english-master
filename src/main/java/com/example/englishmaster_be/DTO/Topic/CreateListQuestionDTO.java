package com.example.englishmaster_be.DTO.Topic;

import com.example.englishmaster_be.DTO.Question.CreateQuestionDTO;

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
