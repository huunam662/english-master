package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.DetailMockTest;
import com.example.englishmaster_be.Model.Question;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailMockTestResponse {

    UUID answerId;

    String answerContent;

    boolean correctAnswer;

    int scoreAnswer;

    public DetailMockTestResponse(DetailMockTest detailMockTest) {

        if(Objects.isNull(detailMockTest)) return;

        this.answerId = detailMockTest.getDetailMockTestId();

        if(Objects.nonNull(detailMockTest.getAnswer())) {
            this.answerContent = detailMockTest.getAnswer().getAnswerContent();
            this.correctAnswer = detailMockTest.getAnswer().isCorrectAnswer();

            Question question = detailMockTest.getAnswer().getQuestion();

            this.scoreAnswer = Objects.nonNull(question) ? question.getQuestionScore() : 0;
        }
    }

}
