package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.DetailMockTest;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DetailMockTestResponse {
    private UUID answerId;
    private String answerContent;
    private boolean correctAnswer;
    private int scoreAnswer;

    public DetailMockTestResponse(DetailMockTest detailMockTest) {
        this.answerId = detailMockTest.getDetailMockTestId();
        this.answerContent = detailMockTest.getAnswer().getAnswerContent();
        this.correctAnswer = detailMockTest.getAnswer().isCorrectAnswer();
        this.scoreAnswer = detailMockTest.getAnswer().getQuestion().getQuestionScore();
    }

}
