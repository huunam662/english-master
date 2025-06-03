package com.example.englishmaster_be.domain.answer.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditAnswer1Request {

    UUID answerId;

    String answerContent;

    String explainDetails;

    Boolean correctAnswer;

}
