package com.example.englishmaster_be.dto.topic;

import com.example.englishmaster_be.dto.question.CreateQuestionByExcelFileDTO;

import java.util.List;

public class CreateListQuestionByExcelFileDTO {
    List<CreateQuestionByExcelFileDTO> questions;

    public CreateListQuestionByExcelFileDTO() {
        super();
    }

    public List<CreateQuestionByExcelFileDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<CreateQuestionByExcelFileDTO> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "CreateListQuestionByExcelFileDTO{" +
                "questions=" + questions +
                '}';
    }
}
