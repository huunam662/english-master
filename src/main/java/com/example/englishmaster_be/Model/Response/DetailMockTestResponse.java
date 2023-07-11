package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.DetailMockTest;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DetailMockTestResponse {
    private UUID answerId;
    private String answerContent;
    private boolean correctAnswer;
    private String explainDetails;

    public DetailMockTestResponse(DetailMockTest detailMockTest){
        this.answerId = detailMockTest.getDetailMockTestId();
        this.answerContent = detailMockTest.getAnswer().getAnswerContent();
        this.correctAnswer = detailMockTest.getAnswer().isCorrectAnswer();
        this.explainDetails = detailMockTest.getAnswer().getExplainDetails();
    }
}
