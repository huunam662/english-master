package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.AnswerBlank;
import com.example.englishmaster_be.Model.Question;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerBlankResponse {

    UUID id;

    QuestionResponse question;

    int position;

    String answer;

    public AnswerBlankResponse(AnswerBlank answerBlank) {
        this.id = answerBlank.getId();
        this.question = new QuestionResponse(answerBlank.getQuestion());
        this.position = answerBlank.getPosition();
        this.answer = answerBlank.getAnswer();
    }

}
