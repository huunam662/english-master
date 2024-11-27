package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.Answer;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

	String createAt;

	String updateAt;

    public AnswerResponse(Answer answer) {

        if(Objects.isNull(answer)) return;

        if(Objects.nonNull(answer.getQuestion()))
            questionId = answer.getQuestion().getQuestionId();

        answerId = answer.getAnswerId();
        answerContent = answer.getAnswerContent();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(answer.getCreateAt()))
            createAt = sdf.format(Timestamp.valueOf(answer.getCreateAt()));
        if(Objects.nonNull(answer.getUpdateAt()))
            updateAt = sdf.format(Timestamp.valueOf(answer.getUpdateAt()));

    }
}
