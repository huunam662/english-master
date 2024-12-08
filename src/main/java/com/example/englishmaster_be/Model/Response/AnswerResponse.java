package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.Answer;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerResponse {

    UUID questionId;

	UUID answerId;

	String answerContent;

    boolean correctAnswer;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
	LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
	LocalDateTime updateAt;

    public AnswerResponse(Answer answer) {

        if(Objects.isNull(answer)) return;

        if(Objects.nonNull(answer.getQuestion()))
            questionId = answer.getQuestion().getQuestionId();

        answerId = answer.getAnswerId();
        answerContent = answer.getAnswerContent();
        this.correctAnswer = answer.isCorrectAnswer();

        this.createAt = answer.getCreateAt();
        this.updateAt = answer.getUpdateAt();

    }
}
