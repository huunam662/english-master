package com.example.englishmaster_be.domain.answer.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Answer1Request {

    String answerContent;

    String explainDetails;

    Boolean correctAnswer;

}
