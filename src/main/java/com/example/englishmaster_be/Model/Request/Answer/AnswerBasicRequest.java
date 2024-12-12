package com.example.englishmaster_be.Model.Request.Answer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerBasicRequest {

    UUID answerId;

    String answerContent;

    Boolean correctAnswer;

}
