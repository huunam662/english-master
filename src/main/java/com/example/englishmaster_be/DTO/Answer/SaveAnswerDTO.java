package com.example.englishmaster_be.DTO.Answer;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveAnswerDTO {

    UUID questionId;

    String contentAnswer;

    String explainDetails;

    boolean correctAnswer;

}
